package github.sgale.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import github.sgale.cardOperations.CardFinder;
import github.sgale.cardOperations.FieldUpdater;
import github.sgale.cardOperations.MediaSaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import static github.sgale.tasks.PropertyGenerator.getSetting;

public class CardOperator {
    private final String input;
    private final String ANKI_URL;
    protected final Gson gson = new Gson();

    public CardOperator(String input) {
        this.input = input;
        this.ANKI_URL = getSetting("ankiUrl");
    }

    public void applyMediaToCards() {
        CardFinder cardFinder = new CardFinder();
        MediaSaver mediaSaver = new MediaSaver(input);

        try {
            long[] cardIds = cardFinder.findByTag();
            if(cardIds.length == 0) {
                throw new NoSuchElementException("Cards not found");
            }

            mediaSaver.store();

            for(long cardId: cardIds) {
                new FieldUpdater(input, cardId).changeField("media");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void translateCardsGlossary() {
        CardFinder cardFinder = new CardFinder();

        try {
            long[] cardIds = cardFinder.findByTag();
            for(long cardId: cardIds) {
                FieldUpdater fieldUpdater = new FieldUpdater(null, cardId);
                if(!fieldUpdater.checkTag("translated")) {
                    String glossary = fieldUpdater.getGlossaryValue();
                    String translatedGlossary = new TextTranslator(glossary).translateGlossary();
                    new FieldUpdater(translatedGlossary, cardId).changeField("glossary");

                    fieldUpdater.addTag("translated");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteModifyTag() {

    }

    protected HttpURLConnection createConnection(String method) throws IOException {
        URL url = new URL(ANKI_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        return conn;
    }

    protected JsonObject createBasicRequest(String action, JsonObject params) {
        JsonObject request = new JsonObject();
        request.addProperty("action", action);
        request.addProperty("version", 6);
        request.add("params", params);
        return request;
    }

    protected String getRawResponse(HttpURLConnection conn) throws IOException {
        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
        }
        finally {
            conn.disconnect();
        }
        return response.toString();
    }

    protected String getInputName() {
        int indexOfSlash = input.lastIndexOf('\\');
        if (indexOfSlash < 0) {
            indexOfSlash = input.lastIndexOf('/');
        }

        return input.substring(indexOfSlash+1);
    }

    protected String getInput() {
        return input;
    }
}

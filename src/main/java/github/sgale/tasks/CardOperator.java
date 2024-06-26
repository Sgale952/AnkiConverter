package github.sgale.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import github.sgale.cardOperations.CardFinder;
import github.sgale.cardOperations.FieldUpdater;
import github.sgale.ankiConverter.Fields;
import github.sgale.cardOperations.MediaSaver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import static github.sgale.tasks.SettingsLoader.getSetting;

public class CardOperator {
    private final String input;
    private final String ANKI_URL;
    private static final Logger log = LogManager.getLogger(CardOperator.class);
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
                log.info("Applying media to card: "+cardId);
                new FieldUpdater(input, cardId).setFieldValue(Fields.MEDIA);
            }
        }
        catch (IOException | NoSuchElementException e) {
            log.error(e);
        }
    }

    public void translateCardsGlossary() {
        CardFinder cardFinder = new CardFinder();

        try {
            long[] cardIds = cardFinder.findByTag();
            for(long cardId: cardIds) {
                log.info("Translating card glossary: "+cardId);
                FieldUpdater fieldUpdater = new FieldUpdater(null, cardId);

                if(!fieldUpdater.checkTag("translated")) {
                    String glossary = fieldUpdater.getFieldValue(Fields.GLOSSARY);
                    String translatedGlossary = new TextTranslator(glossary).translateGlossary();
                    new FieldUpdater(translatedGlossary, cardId).setFieldValue(Fields.TRANSLATED_GLOSSARY);

                    fieldUpdater.addTag("translated");
                }
                else {
                    log.info("Already translated");
                }
            }
        }
        catch (Exception e) {
            log.error(e);
        }
    }

    public void removeModifyTag() throws IOException {
        long[] cardIds = new CardFinder().findByTag();
        for(long cardId: cardIds) {
            log.info("Removing card modify tag: "+cardId);
            FieldUpdater fieldUpdater = new FieldUpdater(null, cardId);
            String audioFieldValue = fieldUpdater.getFieldValue(Fields.AUDIO);
            String imageFieldValue = fieldUpdater.getFieldValue(Fields.IMAGE);

            if(!audioFieldValue.isEmpty() && !imageFieldValue.isEmpty()) {
                fieldUpdater.removeTag(getSetting("tag"));
            }
            else {
                log.info("Found empty media field! Abort removing");
            }
        }
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

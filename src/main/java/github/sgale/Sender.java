package github.sgale;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static github.sgale.Converter.deleteOldFile;
import static github.sgale.Properter.getSetting;

public class Sender {
    private static final String ANKI_URL = getSetting("ankiUrl");
    private static final String IMAGE_FIELD = getSetting("imageField");
    private static final String AUDIO_FIELD = getSetting("audioField");
    private static final String TAG = getSetting("tag");
    private static final Gson gson = new Gson();

    static void applyToCards(String input) {
        try {
            long[] cardIds = CardFinder.findCardsByTag();
            if(cardIds.length==0) {
                System.exit(0);
            }

            MediaSaver.store(input);

            for(long cardId: cardIds) {
                HttpURLConnection conn = createConnection("POST");
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(buildJson(input, cardId).getBytes("utf-8"));
                    System.out.println("Apply to card: " + conn.getResponseCode());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String buildJson(String input, long cardId) {
        JsonObject fields = new JsonObject();
        fields.addProperty(getNoteField(input), getFieldValue(input));

        JsonObject note = new JsonObject();
        note.addProperty("id", cardId);
        note.add("fields", fields);

        JsonObject params = new JsonObject();
        params.add("note", note);

        JsonObject request = new JsonObject();
        request.addProperty("action", "updateNoteFields");
        request.addProperty("version", 6);
        request.add("params", params);

        return gson.toJson(request);
    }

    private static HttpURLConnection createConnection(String method) throws IOException {
        URL url = new URL(ANKI_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        return conn;
    }

    private static String getNoteField(String input) {
        int indexOfDot = input.lastIndexOf('.');
        String fileExtension = input.substring(indexOfDot);
        return fileExtension.equals(".webp")? IMAGE_FIELD : AUDIO_FIELD;
    }

    private static String getFieldValue(String input) {
        String fileName = getFileName(input);
        return getNoteField(input).equals(IMAGE_FIELD)? "<img src="+fileName+">" : "[sound:"+fileName+"]";
    }

    private static String getFileName(String input) {
        int indexOfSlash = input.lastIndexOf('\\');
        if (indexOfSlash<0) {
            indexOfSlash = input.lastIndexOf('/');
        }

        return input.substring(indexOfSlash+1);
    }

    private static class CardFinder {
        static long[] findCardsByTag() throws IOException {
            HttpURLConnection conn = createConnection("GET");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(buildJson().getBytes("utf-8"));
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            System.out.println("Find cards: " + conn.getResponseCode());
            return CardFinder.getResponse(conn);
        }

        static String buildJson() throws IOException {
            JsonObject params = new JsonObject();
            params.addProperty("query", "tag:"+TAG);

            JsonObject request = new JsonObject();
            request.addProperty("action", "findCards");
            request.addProperty("version", 6);
            request.add("params", params);

            return gson.toJson(request);
        }

        static long[] getResponse(HttpURLConnection conn) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())))) {
                StringBuilder response = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }

                JsonArray cardIdsJson = gson.fromJson(response.toString(), JsonObject.class).getAsJsonArray("result");
                long[] cardIds = new long[cardIdsJson.size()];
                for (int i = 0; i < cardIdsJson.size(); i++) {
                    cardIds[i] = cardIdsJson.get(i).getAsLong();
                }

                return cardIds;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                conn.disconnect();
            }
        }
    }

    private static class MediaSaver {
        static void store(String input) throws IOException {
            HttpURLConnection conn = createConnection("POST");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(buildJson(input).getBytes("utf-8"));
            }

            System.out.println("Store media: " + conn.getResponseCode());
            deleteOldFile(input);
        }

        static String buildJson(String input) {
            String fileName = getFileName(input);

            JsonObject params = new JsonObject();
            params.addProperty("filename", fileName);
            params.addProperty("path", input);

            JsonObject request = new JsonObject();
            request.addProperty("action", "storeMediaFile");
            request.addProperty("version", 6);
            request.add("params", params);

            return gson.toJson(request);
        }
    }
}
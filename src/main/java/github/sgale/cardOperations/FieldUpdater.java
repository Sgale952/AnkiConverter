package github.sgale.cardOperations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import github.sgale.tasks.CardOperator;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import static github.sgale.tasks.PropertyGenerator.getSetting;

public class FieldUpdater extends CardOperator {
    private final long cardId;
    private final String input;
    private final String IMAGE_FIELD;
    private final String AUDIO_FIELD;
    private final String GLOSSARY_FIELD;

    public FieldUpdater(String input, long cardId) {
        super(input);
        this.input = input;
        this.cardId = cardId;
        this.IMAGE_FIELD = getSetting("imageField");
        this.AUDIO_FIELD = getSetting("audioField");
        this.GLOSSARY_FIELD = getSetting("glossaryField");
    }

    public void changeField(String field) throws IOException {
        ChangeField changeField = new ChangeField();
        HttpURLConnection conn = createConnection("POST");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(changeField.buildJson(field).getBytes(StandardCharsets.UTF_8));
            System.out.println("Apply to card: " + conn.getResponseCode());
        }
    }

    public String getGlossaryValue() throws IOException {
        GetGlossaryFieldValue getGlossaryFieldValue = new GetGlossaryFieldValue();
        JsonObject jsonObject = JsonParser.parseString(getGlossaryFieldValue.sendRequest()).getAsJsonObject();

        JsonArray resultArray = jsonObject.getAsJsonArray("result");
        JsonObject firstResult = resultArray.get(0).getAsJsonObject();
        JsonObject fieldsObject = firstResult.getAsJsonObject("fields");
        JsonObject frontObject = fieldsObject.getAsJsonObject(GLOSSARY_FIELD);

        return frontObject.getAsJsonPrimitive("value").getAsString();
    }

    private class ChangeField {
        String buildJson(String field) {
            JsonObject fields = new JsonObject();
            switch(field) {
                case "media" -> fields.addProperty(getMediaFieldName(), glueMediaFieldValue());
                case "glossary" -> fields.addProperty(GLOSSARY_FIELD, input);
            }

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

        String getMediaFieldName() {
            int indexOfDot = input.lastIndexOf('.');
            String fileExtension = input.substring(indexOfDot);
            return fileExtension.equals(".webp") ? IMAGE_FIELD : AUDIO_FIELD;
        }

        String glueMediaFieldValue() {
            String fileName = getInputName();
            return getMediaFieldName().equals(IMAGE_FIELD) ? "<img src=" + fileName + ">" : "[sound:" + fileName + "]";
        }
    }

    private class GetGlossaryFieldValue {
        String sendRequest() throws IOException {
            HttpURLConnection conn = createConnection("GET");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(buildJson().getBytes(StandardCharsets.UTF_8));
                System.out.println("Get glossary field: " + conn.getResponseCode());
            }

            return getResponse(conn);
        }

        private String buildJson() {
            JsonObject params = new JsonObject();
            JsonArray notes = new JsonArray();
            notes.add(cardId);
            params.add("notes", notes);

            JsonObject request = new JsonObject();
            request.addProperty("action", "notesInfo");
            request.addProperty("version", 6);
            request.add("params", params);

            return gson.toJson(request);
        }

        private String getResponse(HttpURLConnection conn) throws IOException {
            StringBuilder response = new StringBuilder();

            try (InputStream inputStream = conn.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                String line;
                while((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            return response.toString();
        }
    }
}
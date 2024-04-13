package github.sgale.cardOperations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import github.sgale.tasks.CardOperator;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class FieldUpdater extends CardOperator {
    private final long cardId;
    private final String input;

    public FieldUpdater(String input, long cardId) {
        super(input);
        this.input = input;
        this.cardId = cardId;
    }

    public void setFieldValue(Fields field) throws IOException {
        SetFieldValue setFieldValue = new SetFieldValue();
        HttpURLConnection conn = createConnection("POST");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(setFieldValue.buildJson(field).getBytes(StandardCharsets.UTF_8));
            System.out.println("Apply to card: " + conn.getResponseCode());
        }
    }

    public String getFieldValue(Fields field) throws IOException {
        GetFieldValue getFieldValue = new GetFieldValue();
        JsonObject jsonObject = JsonParser.parseString(getFieldValue.sendRequest()).getAsJsonObject();

        JsonArray resultArray = jsonObject.getAsJsonArray("result");
        JsonObject firstResult = resultArray.get(0).getAsJsonObject();
        JsonObject fieldsObject = firstResult.getAsJsonObject("fields");
        JsonObject valueObject = fieldsObject.getAsJsonObject(field.getKey());

        return valueObject.getAsJsonPrimitive("value").getAsString();
    }

    public boolean checkTag(String tag) throws IOException {
        String[] tags = getTags();
        for(String t: tags) {
            if(t.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public String[] getTags() throws IOException {
        GetTags getTags = new GetTags();
        HttpURLConnection conn = createConnection("GET");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(getTags.buildJson().getBytes(StandardCharsets.UTF_8));
            System.out.println("Get tags: " + conn.getResponseCode());
        }

        return getTags.getResponse(conn);
    }

    public void addTag(String tag) throws IOException {
        changeTag(tag, "addTags");
    }

    public void removeTag(String tag) throws IOException {
        changeTag(tag, "removeTags");
    }

    private void changeTag(String tag, String action) throws IOException {
        ChangeTag changeTag = new ChangeTag();
        HttpURLConnection conn = createConnection("POST");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(changeTag.buildJson(tag, action).getBytes(StandardCharsets.UTF_8));
            System.out.println("Change tag: " + conn.getResponseCode());
        }
    }

    private class SetFieldValue {
        String buildJson(Fields field) {
            JsonObject fields = new JsonObject();
            switch(field) {
                case MEDIA -> fields.addProperty(getMediaFieldName().getKey(), glueMediaFieldValue());
                case GLOSSARY -> fields.addProperty(Fields.GLOSSARY.getKey(), input);
            }

            JsonObject note = new JsonObject();
            note.addProperty("id", cardId);
            note.add("fields", fields);

            JsonObject params = new JsonObject();
            params.add("note", note);

            JsonObject request = createBasicRequest("updateNoteFields", params);
            return gson.toJson(request);
        }

        Fields getMediaFieldName() {
            int indexOfDot = input.lastIndexOf('.');
            String fileExtension = input.substring(indexOfDot);
            return fileExtension.equals(".webp") ? Fields.IMAGE : Fields.AUDIO;
        }

        String glueMediaFieldValue() {
            String fileName = getInputName();
            return getMediaFieldName().equals(Fields.IMAGE) ? "<img src=" + fileName + ">" : "[sound:" + fileName + "]";
        }
    }

    private class GetFieldValue {
        String sendRequest() throws IOException {
            HttpURLConnection conn = createConnection("GET");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(buildJson().getBytes(StandardCharsets.UTF_8));
                System.out.println("Get glossary field: " + conn.getResponseCode());
            }

            return getRawResponse(conn);
        }

        private String buildJson() {
            JsonObject params = new JsonObject();
            JsonArray notes = new JsonArray();
            notes.add(cardId);
            params.add("notes", notes);

            JsonObject request = createBasicRequest("notesInfo", params);
            return gson.toJson(request);
        }
    }

    private class GetTags {
        private String buildJson() {
            JsonObject params = new JsonObject();
            params.addProperty("note", cardId);

            JsonObject request = createBasicRequest("getNoteTags", params);
            return request.toString();
        }

        protected String[] getResponse(HttpURLConnection conn) throws IOException {
            String rawResponse = getRawResponse(conn);
            JsonArray tagsJson = gson.fromJson(rawResponse.toString(), JsonObject.class).getAsJsonArray("result");

            String[] tags = new String[tagsJson.size()];
            for (int i = 0; i < tagsJson.size(); i++) {
                tags[i] = tagsJson.get(i).getAsString();
            }

            return tags;
        }
    }

    private class ChangeTag {
        private String buildJson(String tag, String action) {
            JsonObject params = new JsonObject();
            JsonArray notesArray = new JsonArray();
            notesArray.add(cardId);

            params.add("notes", notesArray);
            params.addProperty("tags", tag);

            JsonObject request = createBasicRequest(action, params);
            return request.toString();
        }
    }
}
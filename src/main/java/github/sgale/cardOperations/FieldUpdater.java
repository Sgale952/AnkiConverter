package github.sgale.cardOperations;

import com.google.gson.JsonObject;
import github.sgale.tasks.CardOperator;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import static github.sgale.tasks.PropertyGenerator.getSetting;

public class FieldUpdater extends CardOperator {
    private final String input = getInput();
    private final String IMAGE_FIELD = getSetting("imageField");
    private final String AUDIO_FIELD = getSetting("audioField");

    public FieldUpdater(String input) {
        super(input);
    }

    public void changeMediaField(long cardId) throws IOException {
        HttpURLConnection conn = createConnection("POST");
        try (OutputStream os = conn.getOutputStream()) {
            os.write(buildJson(cardId).getBytes("utf-8"));
            System.out.println("Apply to card: " + conn.getResponseCode());
        }
    }

    private String buildJson(long cardId) {
        JsonObject fields = new JsonObject();
        fields.addProperty(getNoteField(), getFieldValue());

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

    private String getNoteField() {
        int indexOfDot = input.lastIndexOf('.');
        String fileExtension = input.substring(indexOfDot);
        return fileExtension.equals(".webp")? IMAGE_FIELD : AUDIO_FIELD;
    }

    private String getFieldValue() {
        String fileName = getFileName(input);
        return getNoteField().equals(IMAGE_FIELD)? "<img src="+fileName+">" : "[sound:"+fileName+"]";
    }
}
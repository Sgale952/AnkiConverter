package github.sgale.cardOperations;

import com.google.gson.JsonObject;
import github.sgale.tasks.CardOperator;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class MediaSaver extends CardOperator {
    private final String input = getInput();

    public MediaSaver(String input) {
        super(input);
    }

    public void store() throws IOException {
        HttpURLConnection conn = createConnection("POST");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(buildJson().getBytes("utf-8"));
        }

        System.out.println("Store media: " + conn.getResponseCode());
    }

    private String buildJson() {
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

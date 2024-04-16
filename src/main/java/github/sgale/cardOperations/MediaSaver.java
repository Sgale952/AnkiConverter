package github.sgale.cardOperations;

import com.google.gson.JsonObject;
import github.sgale.tasks.CardOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class MediaSaver extends CardOperator {
    private final String input = getInput();
    private static final Logger log = LogManager.getLogger(MediaSaver.class);

    public MediaSaver(String input) {
        super(input);
    }

    public void store() throws IOException {
        HttpURLConnection conn = createConnection("POST");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(buildJson().getBytes(StandardCharsets.UTF_8));
        }

        log.info("Store media: " + conn.getResponseMessage());
    }

    private String buildJson() {
        String fileName = getInputName();

        JsonObject params = new JsonObject();
        params.addProperty("filename", fileName);
        params.addProperty("path", input);

        JsonObject request = createBasicRequest("storeMediaFile", params);
        return gson.toJson(request);
    }
}

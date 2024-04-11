package github.sgale;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sender {
    private static String DECK_NAME = "日本語 \uD83C\uDDEF\uD83C\uDDF5::練習";
    private static String MODEL_NAME = "Animecards";
    private static String TAG = "unconfigured";

    static void sendImage(String input) {
        try {
            byte[] imageData = Files.readAllBytes(Paths.get(input));
            URL url = new URL("http://localhost:8765");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"action\":\"addNote\",\"version\":6,\"params\":{\"note\":{\"deckName\":\""+DECK_NAME+"\",\"modelName\":\""+MODEL_NAME+"\",\"fields\":{\"Front\":\"<img src='data:image/webp;base64," + java.util.Base64.getEncoder().encodeToString(imageData) + "'>\",\"Back\":\"Your_Back_Text\"},\"tags\":[]}}}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] inputJson = jsonInputString.getBytes("utf-8");
                os.write(inputJson, 0, inputJson.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response code: " + responseCode);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

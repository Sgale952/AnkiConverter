package github.sgale;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Properter {
    private static final String SETTINGS_FILE = "ankiConverter.properties";
    private static final Properties properties = new Properties();

    public static void loadSettingsFile() {
        try (FileInputStream in = new FileInputStream(SETTINGS_FILE)) {
            properties.load(in);
        }
        catch (IOException e) {
            if (!Files.exists(Paths.get(SETTINGS_FILE))) {
                setDefaultSettings();
                System.exit(0);
            }
            else {
                e.printStackTrace();
            }
        }
    }

    private static void setDefaultSettings() {
        setSetting("FFmpegPath", "C:\\Program Files\\FFmpeg\\bin\\ffmpeg.exe");
        setSetting("sendMedia", "true");
        setSetting("ankiUrl", "http://localhost:8765");
        setSetting("imageField", "Picture");
        setSetting("audioField", "SentenceAudio");
        setSetting("tag", "unconfigured");
        saveSettingsFile();
    }

    private static void saveSettingsFile() {
        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(out, "https://github.com/Sgale952/AnkiConverter");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSetting(String key) {
        return properties.getProperty(key);
    }

    private static void setSetting(String key, String value) {
        properties.setProperty(key, value);
    }
}

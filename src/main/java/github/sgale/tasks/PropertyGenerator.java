package github.sgale.tasks;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertyGenerator {
    private static final String SETTINGS_FILE = "ankiConverter.properties";
    private static final Properties properties = new Properties();

    public static void loadSettingsFile() {
        try (FileInputStream propertyFile = new FileInputStream(SETTINGS_FILE)) {
            properties.load(propertyFile);
        }
        catch (IOException e) {
            if(!Files.exists(Paths.get(SETTINGS_FILE))) {
                setDefaultSettings();
                System.exit(0);
            }
            else {
                e.printStackTrace();
            }
        }
    }

    private static void setDefaultSettings() {
        setSetting("convertMedia", "true");
        setSetting("FFmpegPath", "C:\\Program Files\\FFmpeg\\bin\\ffmpeg.exe");

        setSetting("sendMedia", "true");
        setSetting("ankiUrl", "http://localhost:8765");
        setSetting("imageField", "Picture");
        setSetting("audioField", "SentenceAudio");
        setSetting("tag", "unconfigured");

        setSetting("translate", "true");
        setSetting("deeplApiKey", "ee87512a-007a-4db2-8332-5b9e7eb954e3:fx");
        setSetting("glossaryField", "Glossary");
        setSetting("sourceLang", "EN");
        setSetting("targetLang", "RU");

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

package github.sgale.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertyGenerator {
    private static final Logger log = LogManager.getLogger(PropertyGenerator.class);
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
                log.error(e);
            }
        }
    }

    private static void setDefaultSettings() {
        setSetting("logging", "true");
        setSetting("convertMedia", "true");

        setSetting("sendMedia", "true");
        setSetting("ankiUrl", "http://localhost:8765");
        setSetting("imageField", "Picture");
        setSetting("audioField", "SentenceAudio");
        setSetting("tag", "unconfigured");
        setSetting("autoremoveTag", "true");

        setSetting("translateGlossary", "true");
        setSetting("deeplApiKey", "ee87512a-007a-4db2-8332-5b9e7eb954e3:fx");
        setSetting("glossaryField", "PrimaryDefinition");
        setSetting("translatedGlossaryField", "PrimaryDefinition");
        setSetting("targetLang", "RU");

        saveSettingsFile();
    }

    private static void saveSettingsFile() {
        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(out, "https://github.com/Sgale952/AnkiConverter");
            log.info("Properties file successfully created");
        }
        catch (IOException e) {
            log.error(e);
        }
    }

    public static String getFFmpegPath() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                return findFFmpegPath("where");
            }
            return findFFmpegPath("which");
        }
        catch (IOException e) {
            log.error(e);
        }
        return "";
    }

    private static String findFFmpegPath(String command) throws IOException {
        String path;
        Process process = Runtime.getRuntime().exec(command+" ffmpeg");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            path = reader.readLine();
        }
        return path;
    }

    public static String getSetting(String key) {
        return checkProperty(key);
    }

    public static boolean getBoolSetting(String key) {
        return Boolean.parseBoolean(checkProperty(key));
    }

    private static String checkProperty(String key) {
        String property = properties.getProperty(key);
        if(property==null) {
            log.error("Missing property! Update ankiConverter.properties file "+ key);
            System.exit(0);
        }
        return property;
    }

    private static void setSetting(String key, String value) {
        properties.setProperty(key, value);
    }
}

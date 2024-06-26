package github.sgale.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SettingsLoader {
    private static final Logger log = LogManager.getLogger(SettingsLoader.class);
    private static final File SETTINGS_FILE = new File("ankiConverter_settings.ini");
    private static Wini ini = loadSettings();

    public static Wini loadSettings() {
        try {
            return new Wini(SETTINGS_FILE);
        }
        catch (Exception e) {
            createDefaultSettings();
            log.error(e);
            throw new RuntimeException();
        }
    }

    private static void createDefaultSettings() {
        try {
            if (!SETTINGS_FILE.exists()) {
                Files.createFile(SETTINGS_FILE.toPath());
                ini = new Wini(SETTINGS_FILE);
                setDefaultSettings();
                System.exit(0);
            }
        }
        catch (Exception e) {
            log.error(e);
        }
    }

    private static void setDefaultSettings() throws IOException {
        ini.put("modules", "logging", "true");
        ini.put("modules", "convertMedia", "true");
        ini.put("modules", "sendMedia", "true");
        ini.put("modules", "autoremoveTag", "true");
        ini.put("modules", "translateGlossary", "false");

        ini.put("media", "ankiUrl", "http://localhost:8765");
        ini.put("media", "imageField", "Picture");
        ini.put("media", "audioField", "SentenceAudio");
        ini.put("media", "tag", "unconfigured");

        ini.put("translator", "deeplApiKey", "ee87512a-007a-4db2-8332-5b9e7eb954e3:fx");
        ini.put("translator", "targetLang", "RU");
        ini.put("translator", "glossaryField", "PrimaryDefinition");
        ini.put("translator", "translatedGlossaryField", "PrimaryDefinition");

        ini.store();
        log.info("Properties file successfully created");
    }

    public static String getSetting(String option) {
        return findSetting(option);
    }

    public static boolean getBoolSetting(String option) {
        return Boolean.parseBoolean(findSetting(option));
    }

    private static String findSetting(String option) {
        String[] sections = {"modules", "media", "translator"};
        for (String section : sections) {
            String setting = ini.get(section, option);
            if (setting!=null) {
                return setting;
            }
        }
        return settingNotFound(option);
    }

    private static String settingNotFound(String option) {
        log.error("Setting {} is null! Update ankiConverter_settings.ini file", option);
        System.exit(0);
        return "";
    }
}

package github.sgale.ankiConverter;

import static github.sgale.tasks.SettingsLoader.getBoolSetting;

public enum Modules {
    LOGGING(getBoolSetting("logging")),
    CONVERT_MEDIA(getBoolSetting("convertMedia")),
    SEND_MEDIA(getBoolSetting("sendMedia")),
    TRANSLATE_GLOSSARY(getBoolSetting("translateGlossary")),
    AUTOREMOVE_TAG(getBoolSetting("autoremoveTag"));

    private final boolean value;
    Modules(boolean value) {
        this.value = value;
    }

    public boolean getStatus() {
        return value;
    }
}

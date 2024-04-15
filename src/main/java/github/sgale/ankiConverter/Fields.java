package github.sgale.ankiConverter;

import static github.sgale.tasks.PropertyGenerator.getSetting;

public enum Fields {
    MEDIA(""),
    IMAGE(getSetting("imageField")),
    AUDIO(getSetting("audioField")),
    GLOSSARY(getSetting("glossaryField")),
    TRANSLATED_GLOSSARY(getSetting("translatedGlossaryField"));

    private final String fieldKey;
    Fields(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getKey() {
        return fieldKey;
    }
}

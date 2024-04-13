package github.sgale.cardOperations;

import static github.sgale.tasks.PropertyGenerator.getSetting;

public enum Fields {
    MEDIA(""),
    IMAGE(getSetting("imageField")),
    AUDIO(getSetting("audioField")),
    GLOSSARY(getSetting("glossaryField"));

    private final String fieldKey;

    Fields(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getKey() {
        return fieldKey;
    }
}

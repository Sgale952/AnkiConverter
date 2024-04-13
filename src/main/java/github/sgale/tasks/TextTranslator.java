package github.sgale.tasks;

import com.deepl.api.TextResult;
import com.deepl.api.Translator;

import static github.sgale.tasks.PropertyGenerator.getSetting;
import static github.sgale.tasks.PropertyGenerator.loadSettingsFile;

public class TextTranslator {
    private final String DEEPL_KEY = getSetting("deeplApiKey");
    private final String TARGET_LANG = getSetting("targetLang");

    
}

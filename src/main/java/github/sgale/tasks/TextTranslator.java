package github.sgale.tasks;

import com.deepl.api.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static github.sgale.tasks.SettingsLoader.getSetting;

public class TextTranslator {
    private final String input;
    private final String DEEPL_KEY;
    private final String TARGET_LANG;

    public TextTranslator(String input) {
        this.input = input;
        this.DEEPL_KEY = getSetting("deeplApiKey");
        this.TARGET_LANG = getSetting("targetLang");
    }

    public String translateGlossary() throws DeepLException, InterruptedException {
        Document glossary = Jsoup.parse(input);
        Elements sentences = getSentences(glossary);
        translateElements(sentences);
        return glossary.root().outerHtml();
    }

    private Elements getSentences(Document doc) {
        Elements allSentences = new Elements();
        allSentences.addAll(doc.select("ul[data-sc-content=glossary] li"));
        allSentences.addAll(doc.select("div[data-sc-content=xref-glossary]"));
        allSentences.addAll(doc.select("div[style=\"margin-left: 0.5rem;\"]"));

        Elements exampleSentences = doc.select("div[data-sc-content=example-sentence-b]");
        for(Element sentence: exampleSentences) {
            sentence.select("span").remove();
            allSentences.add(sentence);
        }

        return allSentences;
    }

    private void translateElements(Elements sentences) throws DeepLException, InterruptedException {
        for(Element sentence: sentences) {
            String translatedSentence = translateText(sentence.text());
            sentence.text(translatedSentence);
        }
    }

    private String translateText(String text) throws DeepLException, InterruptedException {
        TranslatorOptions translatorOptions = new TranslatorOptions();
        translatorOptions.setSendPlatformInfo(false);

        Translator translator = new Translator(DEEPL_KEY, translatorOptions);

        TextTranslationOptions translationOptions = new TextTranslationOptions();
        translationOptions.setSentenceSplittingMode(SentenceSplittingMode.Off);
        translationOptions.setPreserveFormatting(true);

        return translator.translateText(text, null, TARGET_LANG, translationOptions).getText();
    }
}
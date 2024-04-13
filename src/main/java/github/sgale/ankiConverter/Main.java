package github.sgale.ankiConverter;

import github.sgale.tasks.CardOperator;
import github.sgale.tasks.MediaConverter;

import java.io.File;
import java.io.IOException;

import static github.sgale.tasks.PropertyGenerator.getSetting;
import static github.sgale.tasks.PropertyGenerator.loadSettingsFile;

public class Main {
    public static void main(String[] args) {
        loadSettingsFile();
        String input = getMediaPath(args);
        String output = getMediaPath(args);

        try {
            if(getBoolSetting("convertMedia")) {
                MediaConverter mediaConverter = new MediaConverter(input);
                output = mediaConverter.switchMediaConverter();
                deleteInitialFile(input);
            }
            if(getBoolSetting("sendMedia")) {
                CardOperator cardOperator = new CardOperator(output);
                cardOperator.applyMediaToCards();
                deleteInitialFile(output);
            }
            if(getBoolSetting("translate")) {
                CardOperator cardOperator = new CardOperator(null);
                cardOperator.translateCardsGlossary();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean getBoolSetting(String key) {
        return Boolean.parseBoolean(getSetting(key));
    }

    private static String getMediaPath(String[] args) throws IllegalArgumentException {
        for(String arg:args) {
            if(arg.contains("/") || arg.contains("\\")) {
                return arg;
            }
        }
        throw new IllegalArgumentException("Incorrect argument");
    }

    private static void deleteInitialFile(String input) {
        File file = new File(input);
        file.delete();
    }
}
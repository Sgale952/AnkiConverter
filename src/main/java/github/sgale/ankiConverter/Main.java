package github.sgale.ankiConverter;

import github.sgale.tasks.CardOperator;
import github.sgale.tasks.MediaConverter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static github.sgale.tasks.PropertyGenerator.getSetting;
import static github.sgale.tasks.PropertyGenerator.loadSettingsFile;

public class Main {
    public static void main(String[] rawArgs) {
        try {
            loadSettingsFile();
            HashMap<String, String> args = getArg(rawArgs);
            String input = args.get("mediaPath");
            String output = args.get("mediaPath");

            if(getBoolSetting("convertMedia")) {
                MediaConverter mediaConverter = new MediaConverter(input);
                output = mediaConverter.switchMediaConverter(args.get("convertType"));
                deleteInitialFile(input);
            }
            if(getBoolSetting("sendMedia")) {
                CardOperator cardOperator = new CardOperator(output);
                cardOperator.applyMediaToCards();
                deleteInitialFile(output);
            }
            if(getBoolSetting("translate")) {

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean getBoolSetting(String key) {
        return Boolean.parseBoolean(getSetting(key));
    }

    private static HashMap<String, String> getArg(String[] args) {
        String mediaPath;
        String convertType;
        HashMap<String, String> argsMap = new HashMap<>();

        for(String arg:args) {
            if(arg.startsWith("-")) {
                convertType = arg;
                argsMap.put("convertType", convertType);
            }
            if(arg.contains("/") || arg.contains("\\")) {
                mediaPath = arg;
                argsMap.put("mediaPath", mediaPath);
            }
        }

        return argsMap;
    }

    private static void deleteInitialFile(String input) {
        File file = new File(input);
        file.delete();
    }
}
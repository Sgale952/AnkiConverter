package github.sgale;

import java.io.IOException;

import static github.sgale.Converter.*;
import static github.sgale.Properter.getSetting;
import static github.sgale.Properter.loadSettingsFile;
import static github.sgale.Sender.applyToCards;

public class Main {
    private static String taskSwitcher(String[] args) throws IOException {
        switch (args[0]) {
            case "-aac" -> {
                return convertAac(args[1]);
            }
            case "-webp" -> {
                return convertWebp(args[1]);
            }
            default -> throw new IllegalArgumentException("Incorrect arguments");
        }
    }

    public static void main(String[] args) {
        try {
            loadSettingsFile();
            boolean isSendMedia = Boolean.parseBoolean(getSetting("sendMedia"));

            String output = taskSwitcher(args);
            if(isSendMedia) {
                applyToCards(output);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
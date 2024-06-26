package github.sgale.ankiConverter;

import github.sgale.tasks.CardOperator;
import github.sgale.tasks.MediaConverter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;

import static github.sgale.tasks.SettingsLoader.*;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Program starting...");
        loadSettings();

        try {
            String input = getMediaPath(args);
            String output = getMediaPath(args);

            if(!Modules.LOGGING.getStatus()) {
                Configurator.setRootLevel(Level.OFF);
            }
            if(Modules.CONVERT_MEDIA.getStatus()) {
                MediaConverter mediaConverter = new MediaConverter(input);
                output = mediaConverter.switchMediaConverter();
                deleteInitialFile(input);
            }
            if(Modules.SEND_MEDIA.getStatus()) {
                CardOperator cardOperator = new CardOperator(output);
                cardOperator.applyMediaToCards();
                deleteInitialFile(output);
            }
            if(Modules.TRANSLATE_GLOSSARY.getStatus()) {
                CardOperator cardOperator = new CardOperator(null);
                cardOperator.translateCardsGlossary();
            }
            if(Modules.AUTOREMOVE_TAG.getStatus()) {
                CardOperator cardOperator = new CardOperator(null);
                cardOperator.removeModifyTag();
            }
        }
        catch (Exception e) {
            log.error(e);
        }
    }

    private static String getMediaPath(String[] args) throws IllegalArgumentException {
        for(String arg:args) {
            if(arg.contains("/") || arg.contains("\\")) {
                return arg;
            }
        }
        throw new IllegalArgumentException("Incorrect path");
    }

    private static void deleteInitialFile(String input) {
        File file = new File(input);
        file.delete();
    }
}
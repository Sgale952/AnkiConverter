package github.sgale;

import java.io.IOException;

import static github.sgale.Converter.*;

public class Main {
    private static String taskSwitcher(String[] args) throws IOException {
        switch (args[0]) {
            case "-aac" -> {
                return convertAac(args[1]);
            }
            case "-webp" -> {
                return convertWebp(args[1]);
            }
            default -> throw new IllegalArgumentException("");
        }
    }

    public static void main(String[] args) {
        try {
            String output = taskSwitcher(args);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
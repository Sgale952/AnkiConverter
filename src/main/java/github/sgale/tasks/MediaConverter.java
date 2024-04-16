package github.sgale.tasks;

import com.luciad.imageio.webp.WebPImageWriterSpi;
import com.luciad.imageio.webp.WebPWriteParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class MediaConverter {
    private final String input;
    private final Logger log = LogManager.getLogger(MediaConverter.class);

    public MediaConverter(String input) {
        this.input = input;
    }

    public String switchMediaConverter() throws IOException, InterruptedException {
        try {
            return convertToWebp();
        }
        catch (IOException | IllegalArgumentException e) {
            log.warn(e);
            log.warn("Trying convert to aac...");
            Files.delete(Path.of(getOutputPath(input, ".webp")));
            return convertToAac();
        }
    }

    private String convertToAac() throws IOException, InterruptedException {
        String output = getOutputPath(input, ".aac");
        executeFFmpeg(output);
        return output;
    }

    private void executeFFmpeg(String output) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", input, "-c:a", "aac", output);
        Process command = pb.start();
        int exitCode = command.waitFor();
        if(exitCode != 0) {
            String errorMessage = getFFmpegErrorMessage(command);
            throw new IOException("FFmpeg failed with exit code: " + exitCode + ". Error message: " + errorMessage);
        }
    }

    private String getFFmpegErrorMessage(Process process) {
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            StringBuilder errorMessage = new StringBuilder();
            String line;
            while((line = errorReader.readLine()) != null) {
                errorMessage.append(line).append("\n");
            }
            return errorMessage.toString();
        }
        catch (IOException e) {
            log.error(e);
            return "";
        }
    }

    private String convertToWebp() throws IOException {
        File inputFile = new File(input);
        File outputFile = new File(getOutputPath(input, ".webp"));
        Locale locale = new Locale("en", "US");

        try (ImageOutputStream output = ImageIO.createImageOutputStream(outputFile)) {
            BufferedImage image = ImageIO.read(inputFile);

            ImageWriterSpi writerSpi = new WebPImageWriterSpi();
            ImageWriter writer = writerSpi.createWriterInstance();

            writer.setOutput(output);
            WebPWriteParam webpWriteParam = new WebPWriteParam(locale);
            writer.write(null, new IIOImage(image, null, null), webpWriteParam);

            writer.dispose();
        }

        return outputFile.toString();
    }

    private String getOutputPath(String input, String extension) {
        int dotIndex = input.lastIndexOf('.');
        return input.substring(0, dotIndex)+extension;
    }
}

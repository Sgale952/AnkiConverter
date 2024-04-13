package github.sgale.tasks;

import com.luciad.imageio.webp.WebPImageWriterSpi;
import com.luciad.imageio.webp.WebPWriteParam;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static github.sgale.tasks.PropertyGenerator.getSetting;

public class MediaConverter {
    private final String input;
    private final String FFMPEG_PATH = getSetting("FFmpegPath");

    public MediaConverter(String input) {
        this.input = input;
    }

    public String switchMediaConverter(String type) throws IOException {
        switch (type) {
            case "-aac" -> {
                return convertToAac();
            }
            case "-webp" -> {
                return convertToWebp();
            }
            default -> throw new IllegalArgumentException("Incorrect arguments");
        }
    }

    private String convertToAac() throws IOException {
        FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
        String output = getOutputPath(input, ".aac");

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input)
                .overrideOutputFiles(true)
                .addOutput(output)
                .setAudioCodec("aac")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
        executor.createJob(builder).run();

        return output;
    }

    private String convertToWebp() throws IOException {
        File inputFile = new File(input);
        File outputFile = new File(getOutputPath(input, ".webp"));
        Locale locale = new Locale("en", "US");

        try(ImageOutputStream output = ImageIO.createImageOutputStream(outputFile)) {
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

package github.sgale;

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

public class Converter {
    private static final String FFMPEG_DIR = "C:\\Program Files\\FFmpeg\\bin\\ffmpeg.exe";

    public static String convertAac(String input) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(FFMPEG_DIR);
        String output = getOutput(input, ".aac");

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input)
                .overrideOutputFiles(true)
                .addOutput(output)
                .setAudioCodec("aac")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
        executor.createJob(builder).run();

        deleteUnconvertedFile(input);

        return output;
    }

    public static String convertWebp(String input) throws IOException {
        File inputFile = new File(input);
        File outputFile = new File(getOutput(input, ".webp"));
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

        deleteUnconvertedFile(input);

        return outputFile.toString();
    }

    private static String getOutput(String input, String extension) {
        int dotIndex = input.lastIndexOf('.');
        return input.substring(0, dotIndex)+extension;
    }

    private static void deleteUnconvertedFile(String input) {
        File file = new File(input);
        file.delete();
    }
}

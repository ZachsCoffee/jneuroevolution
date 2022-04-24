package convolution_test.stl10;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Stl10ToImages {

    public static final int IMAGE_DIMENSION = 96;

    public static void main(String[] args) {
        Path basePath = Paths.get("~/Develop/MachineLearning/stl10_binary");

        extractToImages(basePath.resolve("train_X.bin"), basePath.resolve("images/train"));
        extractToImages(basePath.resolve("test_X.bin"), basePath.resolve("images/test"));
    }

    private static void extractToImages(Path binFile, Path destination) {
        int channelSize = IMAGE_DIMENSION * IMAGE_DIMENSION;
        byte[] red = new byte[channelSize], green = new byte[channelSize], blue = new byte[channelSize];

        try (
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(binFile.toAbsolutePath().toString()))
        ) {
            BufferedImage image = new BufferedImage(IMAGE_DIMENSION, IMAGE_DIMENSION, BufferedImage.TYPE_INT_RGB);
            int count = 0;
            while (
                inputStream.read(red) != - 1 &&
                    inputStream.read(green) != - 1 &&
                    inputStream.read(blue) != - 1
            ) {
                for (int i = 0; i < IMAGE_DIMENSION; i++) {
                    for (int j = 0; j < IMAGE_DIMENSION; j++) {
                        int position = vectorPosition(j, i);
                        image.setRGB(j, i, rgb(red[position], green[position], blue[position]));
                    }
                }

                ImageIO.write(image, "jpg", destination.resolve(String.valueOf(count)).toFile());
                count++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int vectorPosition(int i, int j) {
        return i * IMAGE_DIMENSION + j;
    }

    private static int rgb(byte r, byte g, byte b) {
        return Byte.toUnsignedInt(r) << 16 | Byte.toUnsignedInt(g) << 8 | Byte.toUnsignedInt(b);
    }
}

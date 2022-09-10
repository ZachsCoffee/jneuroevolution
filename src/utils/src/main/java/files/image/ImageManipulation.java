package files.image;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ImageManipulation {

    public static BufferedImage scale(BufferedImage bufferedImage, double factor) {
        return scale(bufferedImage, factor, factor);
    }

    public static BufferedImage scale(BufferedImage bufferedImage, double widthFactor, double heightFactor) {
        Objects.requireNonNull(bufferedImage);

        BufferedImage scaledBufferedImage = new BufferedImage(
                (int) (bufferedImage.getWidth() * widthFactor),
                (int) (bufferedImage.getHeight() * heightFactor),
                bufferedImage.getType()
        );

        scaledBufferedImage
                .createGraphics()
                .drawRenderedImage(
                    bufferedImage,
                    AffineTransform.getScaleInstance(widthFactor, heightFactor)
                );

        return scaledBufferedImage;
    }

    public static void main(String[] args) {
        String inputPath = "/home/zachs/Develop/Java/artificialintelligence/datasets/input_images";
        double factor = .1;
        for (int i = 2; i <= 22; i++) {
            try {
                ImageIO.write(
                        scale(ImageIO.read(new File(inputPath + "/f" + i + ".jpg")), factor),
                        "jpg",
                        new File(inputPath+"/scaled"+"/f" + i + ".jpg")
                );

                ImageIO.write(
                        scale(ImageIO.read(new File(inputPath+"/f"+i+"_final.jpg")), factor),
                        "jpg",
                        new File(inputPath+"/scaled"+"/f"+i+"_final.jpg")
                );
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

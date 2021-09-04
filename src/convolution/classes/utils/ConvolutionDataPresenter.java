package utils;

import maths.matrix.MatrixReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ConvolutionDataPresenter {
    public static void toBlackWhiteImage(MatrixReader convolutionOutput, File outputImage) {
        try {
            int rows = convolutionOutput.getRowCount();
            int columns = convolutionOutput.getColumnCount();

            BufferedImage bufferedImage = new BufferedImage(columns, rows, BufferedImage.TYPE_BYTE_GRAY);

            for (int i=0; i<rows; i++) {
                for (int j=0; j<columns; j++) {
                    int value = (int) (255 * convolutionOutput.valueAt(i, j));

                    bufferedImage.setRGB(j, i, (value << 16) | (value << 8) | value);
                }
            }

            ImageIO.write(bufferedImage, "jpg", outputImage);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to open for read the image: "+outputImage, e);
        }
    }
}

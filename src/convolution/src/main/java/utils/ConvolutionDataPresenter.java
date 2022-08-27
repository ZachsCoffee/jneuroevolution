package utils;

import core.layer.MatrixReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ConvolutionDataPresenter {
    public static void toBlackWhiteImage(MatrixReader convolutionOutput, File outputImage) {
        try {
            int rows = convolutionOutput.getRowsCount();
            int columns = convolutionOutput.getColumnsCount();

            BufferedImage bufferedImage = new BufferedImage(columns, rows, BufferedImage.TYPE_BYTE_GRAY);

            for (int i=0; i<rows; i++) {
                for (int j=0; j<columns; j++) {
                    double test = convolutionOutput.valueAt(i, j);
                    int value = 255 * (int) convolutionOutput.valueAt(i, j);
//                    if (test < 0 || test > 1) {
//                        throw new RuntimeException("Failed "+test);
//                    }
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

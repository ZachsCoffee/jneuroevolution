package input;

import core.layer.MatrixReader;
import maths.matrix.Matrix2D;

import java.awt.image.BufferedImage;

public class RawImageInput implements ConvolutionInput {

    private final MatrixReader[] channels;

    public RawImageInput(BufferedImage image) {
        int imageHeight = image.getHeight();
        int imageWidth = image.getWidth();
        double[][][] rawData = new double[3][imageHeight][imageWidth];

        for (int height = 0; height < imageHeight; height++) {
            for (int width = 0; width < imageWidth; width++) {
                int rgb = image.getRGB(width, height);

                rawData[0][height][width] = rgb >> 16 & 0xFF;
                rawData[1][height][width] = rgb >> 8 & 0xFF;
                rawData[2][height][width] = rgb & 0xFF;
            }
        }

        channels = new MatrixReader[]{
            new Matrix2D(rawData[0]),
            new Matrix2D(rawData[1]),
            new Matrix2D(rawData[2]),
        };
    }

    @Override
    public MatrixReader[] getChannels() {
        return channels;
    }
}

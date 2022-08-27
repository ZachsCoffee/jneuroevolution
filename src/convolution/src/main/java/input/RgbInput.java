package input;

import core.layer.MatrixReader;

import java.awt.image.BufferedImage;

public class RgbInput implements ConvolutionInput{

    private final MatrixReader[] channels;

    public RgbInput(BufferedImage image) {
        channels = new MatrixReader[] {
            new AbstractImageInput(image) {
                @Override
                protected double readValueOf(int rgb) {
                    return getRed(rgb);
                }
            },
            new AbstractImageInput(image) {
                @Override
                protected double readValueOf(int rgb) {
                    return getGreen(rgb);
                }
            },
            new AbstractImageInput(image) {
                @Override
                protected double readValueOf(int rgb) {
                    return getBlue(rgb);
                }
            },
        };
    }

    @Override
    public MatrixReader[] getChannels() {
        return channels;
    }
}

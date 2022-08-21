package input;

import core.layer.MatrixReader;

import java.awt.image.BufferedImage;
import java.util.Objects;


public class HsbInput implements ConvolutionInput {

    private final MatrixReader[] channels;

    public HsbInput(BufferedImage image) {
        Objects.requireNonNull(image);

        channels = new MatrixReader[] {
                new AbstractImageInput(image) {
                    @Override
                    protected double readValueOf(int rgb) {
                        return getHsb(rgb)[0];
                    }
                },
                new AbstractImageInput(image) {
                    @Override
                    protected double readValueOf(int rgb) {
                        return getHsb(rgb)[1];
                    }
                },
                new AbstractImageInput(image) {
                    @Override
                    protected double readValueOf(int rgb) {
                        return getHsb(rgb)[2];
                    }
                }
        };
    }

    @Override
    public MatrixReader[] getChannels() {
        return channels;
    }
}

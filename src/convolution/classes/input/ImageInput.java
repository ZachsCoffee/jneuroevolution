package input;

import core.layer.MatrixReader;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class ImageInput implements ConvolutionInput {

    private final MatrixReader[] channels;

    public ImageInput(BufferedImage image) {
        Objects.requireNonNull(image);



        channels = new MatrixReader[] {
                new AbstractImageInput(image) {
                    @Override
                    protected double readValueOf(int rgb) {
                        return rgb;
                    }
                }
        };
    }

    @Override
    public MatrixReader[] getChannels() {
        return channels;
    }
}

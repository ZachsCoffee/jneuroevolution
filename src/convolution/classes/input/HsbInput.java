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
                    public double valueAt(int rowIndex, int columnIndex) {
                        return getHsb(rowIndex, columnIndex)[0];
                    }
                },
                new AbstractImageInput(image) {
                    @Override
                    public double valueAt(int rowIndex, int columnIndex) {
                        return getHsb(rowIndex, columnIndex)[1];
                    }
                },
                new AbstractImageInput(image) {
                    @Override
                    public double valueAt(int rowIndex, int columnIndex) {
                        return getHsb(rowIndex, columnIndex)[2];
                    }
                }
        };
    }

    @Override
    public MatrixReader[] getChannels() {
        return channels;
    }
}

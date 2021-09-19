package input;

import maths.matrix.MatrixReader;

import java.awt.image.BufferedImage;
import java.io.File;
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

                    @Override
                    public double[] getRow(int position) {
                        throw new UnsupportedOperationException();
                    }
                },
                new AbstractImageInput(image) {
                    @Override
                    public double valueAt(int rowIndex, int columnIndex) {
                        return getHsb(rowIndex, columnIndex)[1];
                    }

                    @Override
                    public double[] getRow(int position) {
                        throw new UnsupportedOperationException();
                    }
                },
                new AbstractImageInput(image) {
                    @Override
                    public double valueAt(int rowIndex, int columnIndex) {
                        return getHsb(rowIndex, columnIndex)[2];
                    }

                    @Override
                    public double[] getRow(int position) {
                        throw new UnsupportedOperationException();
                    }
                }
        };
    }

    @Override
    public MatrixReader[] getChannels() {
        return channels;
    }
}

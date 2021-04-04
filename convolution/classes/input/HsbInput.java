package input;

import maths.matrix.MatrixReader;

import java.io.File;
import java.util.Objects;


public class HsbInput implements ImageInput {

    private final File image;
    private final MatrixReader[] channels;

    public HsbInput(File image) {
        this.image = Objects.requireNonNull(image);

        channels = new MatrixReader[]{
                new AbstractImageInput(image) {
                    @Override
                    public double valueAt(int rowIndex, int columnIndex) {
                        return getHsb(columnIndex, rowIndex)[0];
                    }

                    @Override
                    public double[] getRow(int position) {
                        throw new UnsupportedOperationException();
                    }
                },
                new AbstractImageInput(image) {
                    @Override
                    public double valueAt(int rowIndex, int columnIndex) {
                        return getHsb(columnIndex, rowIndex)[1];
                    }

                    @Override
                    public double[] getRow(int position) {
                        throw new UnsupportedOperationException();
                    }
                },
                new AbstractImageInput(image) {
                    @Override
                    public double valueAt(int rowIndex, int columnIndex) {
                        return getHsb(columnIndex, rowIndex)[2];
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

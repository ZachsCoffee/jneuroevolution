package input;

import maths.matrix.MatrixReader;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class ImageInput implements ConvolutionInput {

    private final MatrixReader[] channels;

    public ImageInput(BufferedImage image) {
        Objects.requireNonNull(image);

        channels = new MatrixReader[] {
                new AbstractImageInput(image) {
                    @Override
                    public float valueAt(int rowIndex, int columnIndex) {
                        return bufferedImage.getRGB(columnIndex, rowIndex);
                    }

                    @Override
                    public float[] getRow(int position) {
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

package builder;

import maths.matrix.MatrixReader;

public class ConvolutionBuilder {

    public static ConvolutionBuilder initialize(MatrixReader[] channels) {
        return new ConvolutionBuilder(channels);
    }

    private final MatrixReader[] channels;

    private ConvolutionBuilder(MatrixReader[] channels) {

        this.channels = channels;
    }
}

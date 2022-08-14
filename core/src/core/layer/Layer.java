package core.layer;

public interface Layer {
    MatrixReader[] execute(MatrixReader[] inputChannels);

    MatrixSchema[] getSchema(MatrixSchema[] inputChannels, ConvolutionSchemaPrinter convolutionSchemaPrinter);
}

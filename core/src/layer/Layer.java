package layer;

public interface Layer {
    MatrixReader[] computeLayer(MatrixReader[] channels);

    MatrixSchema[] getSchema(MatrixSchema[] channels, ConvolutionSchemaPrinter convolutionSchemaPrinter);
}

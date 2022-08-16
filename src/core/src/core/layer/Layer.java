package core.layer;

public interface Layer extends Imitable<Layer>{
    MatrixReader[] execute(MatrixReader[] inputChannels);

    MatrixSchema[] getSchema(MatrixSchema[] inputChannels, ConvolutionSchemaPrinter convolutionSchemaPrinter);
}

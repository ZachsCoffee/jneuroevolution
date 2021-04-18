package layers;

import maths.matrix.MatrixReader;
import maths.matrix.MatrixSchema;
import schema.ConvolutionSchemaPrinter;

public interface Layer {
    MatrixReader[] computeLayer(MatrixReader[] input);

    MatrixSchema[] toString(MatrixSchema[] input, ConvolutionSchemaPrinter convolutionSchemaPrinter);
}

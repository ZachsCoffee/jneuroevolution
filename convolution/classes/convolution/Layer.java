package convolution;

import dnl.utils.text.table.TextTable;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixSchema;
import schema.ConvolutionSchema;

public interface Layer {
    MatrixReader[] computeLayer(MatrixReader[] input);

    MatrixSchema[] toString(MatrixSchema[] input, ConvolutionSchema convolutionSchema);
}

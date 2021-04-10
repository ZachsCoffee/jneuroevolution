package convolution;

import maths.matrix.MatrixReader;
import maths.matrix.MatrixSchema;
import maths.matrix.QubeSchema;
import schema.ConvolutionSchema;

public interface Layer {
    MatrixReader[] computeLayer(MatrixReader[] input);

    MatrixReader[] computeLayer(MatrixReader[] input, QubeSchema qubeSchema);

    MatrixSchema[] toString(MatrixSchema[] input, ConvolutionSchema convolutionSchema);
}

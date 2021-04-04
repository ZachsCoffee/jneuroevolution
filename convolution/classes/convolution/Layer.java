package convolution;

import maths.matrix.MatrixReader;
import maths.matrix.MatrixSchema;

public interface Layer {
    MatrixReader[] computeLayer(MatrixReader[] input);

    MatrixSchema[] toString(MatrixSchema[] input, StringBuilder stringBuilder);
}

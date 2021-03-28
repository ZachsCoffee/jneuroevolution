package convolution;

import maths.matrix.MatrixReader;

public interface Layer {
    MatrixReader computeLayer(MatrixReader input);
}

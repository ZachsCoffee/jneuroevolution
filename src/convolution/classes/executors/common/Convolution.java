package executors.common;

import maths.matrix.MatrixReader;

public interface Convolution {
    MatrixReader[] execute(MatrixReader[] channels);

    void printSchema(MatrixReader[] channels);
}

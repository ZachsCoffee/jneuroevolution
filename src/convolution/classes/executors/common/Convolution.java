package executors.common;

import layer.MatrixReader;

public interface Convolution {
    MatrixReader[] execute(MatrixReader[] channels);

    void printSchema(MatrixReader[] channels);
}

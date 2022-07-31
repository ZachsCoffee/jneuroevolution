package filters;

import layer.MatrixReader;

public interface Kernel {
    double compute(MatrixReader matrixReader, int startRowIndex, int startColumnIndex);

    int getKernelSize();
}

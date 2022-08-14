package filters;

import core.layer.MatrixReader;

public interface Kernel {
    double compute(MatrixReader matrixReader, int startRowIndex, int startColumnIndex);

    int getKernelSize();
}

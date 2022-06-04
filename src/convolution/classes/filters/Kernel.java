package filters;

import maths.matrix.MatrixReader;

public interface Kernel {
    double compute(MatrixReader matrixReader, int startRowIndex, int startColumnIndex);

    int getKernelSize();
}

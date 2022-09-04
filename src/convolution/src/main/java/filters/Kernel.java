package filters;

import core.layer.MatrixReader;

import java.util.Iterator;

public interface Kernel {
    Iterator<Double> compute(MatrixReader matrixReader, int startRowIndex, int startColumnIndex);

    int getKernelSize();
}

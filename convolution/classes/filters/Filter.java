package filters;

import maths.matrix.MatrixReader;
import maths.Function;
import maths.matrix.Matrix;

import java.util.Objects;

public class Filter {

    private final Function outputFunction;
    private final double[][] kernel;

    public Filter(double[][] kernel) {
        this(kernel, null);
    }

    public Filter(double[][] kernel, Function outputFunction) {
        this.kernel = Objects.requireNonNull(kernel);

        if (! Matrix.isSquare(kernel)) {
            throw new IllegalArgumentException("Kernel must be a square matrix!");
        }

        this.outputFunction = outputFunction;
    }

    public int getKernelSize() {
        return kernel.length;
    }

    /**
     * Applies the filter into the given input. The two indexes describes the starting position of the filter into the input.
     * @param rowIndex The row index to start the filter.
     * @param columnIndex The column index to start the filter.
     * @return The result.
     */
    public double compute(int rowIndex, int columnIndex, MatrixReader matrixReader) {
        Objects.requireNonNull(matrixReader);
        if (matrixReader.getRowCount() == 0) {
            throw new IllegalArgumentException("Argument input don't have any rows!");
        }

        int rowLength = rowIndex + kernel.length;
        int columnLength = columnIndex + kernel.length;

        double sum = 0;
        for (int i = rowIndex, ki = 0; i < rowLength; i++, ki++) {
            for (int j = columnIndex, kj = 0; j < columnLength; j++, kj++) {
                // if kernel value is zero then no need to compute
                // if the input is outbounds no need to compute. (Is a valid case because the layers.convolution can have padding)
                if (kernel[ki][kj] == 0 || i < 0 || i >= matrixReader.getRowCount() || j < 0 || j >= matrixReader.getColumnCount()) {
                    continue;
                }

                sum += matrixReader.valueAt(i, j) * kernel[ki][kj];
            }
        }

        return outputFunction != null
                ? outputFunction.compute(sum)
                : sum;
    }
}

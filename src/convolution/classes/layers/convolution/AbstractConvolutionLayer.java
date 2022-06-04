package layers.convolution;

import filters.Kernel;
import layers.Layer;
import maths.matrix.MatrixRW;
import maths.matrix.MatrixReader;
import maths.matrix.Matrix2D;
import schema.SchemaComputer;

import java.util.Objects;

public abstract class AbstractConvolutionLayer implements Layer {

    protected MatrixRW computeForKernel(
        MatrixReader channel,
        SchemaComputer schemaComputer,
        Kernel kernel
    ) {
        Objects.requireNonNull(channel);

        int inputRows = channel.getRowsCount();
        int inputColumns = channel.getColumnsCount();
        int kernelSize = kernel.getKernelSize();

        schemaComputer.compute(inputRows, inputColumns, kernelSize);

        double[][] output = new double[schemaComputer.getRowsCount()][schemaComputer.getColumnsCount()];

        int inputRowsBound = inputRows + schemaComputer.getPaddingRows() - kernelSize;
        int inputColumnsBound = inputColumns + schemaComputer.getPaddingColumns() - kernelSize;

        try {
            for (
                int i = - schemaComputer.getPaddingRows(), outI = 0;
                i < inputRowsBound;
                i += schemaComputer.getStrideRows(), outI++
            ) {
                for (
                    int j = - schemaComputer.getPaddingColumns(), outJ = 0;
                    j < inputColumnsBound;
                    j += schemaComputer.getStrideColumns(), outJ++
                ) {
                    output[outI][outJ] = kernel.compute(channel, i, j);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new RuntimeException(
                "Input rows bound: " + inputColumnsBound + " input columns bound: " + inputColumnsBound,
                ex
            );
        }

        return new Matrix2D(output);
    }
}

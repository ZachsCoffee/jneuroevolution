package layers.convolution;

import filters.Kernel;
import core.layer.Layer;
import core.layer.MatrixReader;
import maths.matrix.MatrixRW;
import maths.matrix.Matrix2D;
import schema.BluePrint;
import schema.SchemaComputer;

import java.util.ArrayList;
import java.util.Iterator;
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

        BluePrint bluePrint = schemaComputer.compute(inputRows, inputColumns, kernelSize);

        double[][] output = new double[bluePrint.getRowsCount()][bluePrint.getColumnsCount()];

        int inputRowsBound = inputRows + bluePrint.getPaddingRows() - kernelSize;
        int inputColumnsBound = inputColumns + bluePrint.getPaddingColumns() - kernelSize;

        ArrayList<Iterator<Double>> yielders = new ArrayList<>(inputColumnsBound);

        try {
            for (
                int i = - bluePrint.getPaddingRows(), outI = 0;
                i < inputRowsBound;
                i += bluePrint.getStrideRows(), outI++
            ) {
                for (int r = i; r < i + kernelSize; r++) {
                    for (
                        int j = - bluePrint.getPaddingColumns(), outJ = 0;
                        j < inputColumnsBound;
                        j += bluePrint.getStrideColumns(), outJ++
                    ) {
                        if (outJ >= yielders.size()) {
                            yielders.add(kernel.compute(channel, i, j));
                        }

                        output[outI][outJ] = yielders.get(outJ).next();
                    }
                }

                yielders.clear();
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

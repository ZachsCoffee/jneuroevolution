package layers.flatten;

import core.layer.ConvolutionSchemaPrinter;
import core.layer.Layer;
import core.layer.MatrixReader;
import core.layer.MatrixSchema;
import maths.matrix.VectorReader;
import core.schema.LayerSchema;

public class FlatLayer implements Layer {

    @Override
    public MatrixReader[] execute(MatrixReader[] inputChannels) {
        double[] flat = new double[computeOutputSize(inputChannels)];

        int startCopyIndex = 0;
        int rows, columns;
        for (MatrixReader filter : inputChannels) {
            rows = filter.getRowsCount();
            columns = filter.getColumnsCount();
            for (int i=0; i<rows; i++) {
                System.arraycopy(filter.getRow(i), 0, flat, startCopyIndex, columns);
                startCopyIndex += columns;
            }
        }

        return new MatrixReader[] {
                new VectorReader(flat)
        };
    }

    @Override
    public MatrixSchema[] getSchema(MatrixSchema[] inputChannels, ConvolutionSchemaPrinter convolutionSchemaPrinter) {
        int outputSize = computeOutputSize(inputChannels);

        convolutionSchemaPrinter.addRow(
            "Flatten",
            inputChannels.length,
            '-',
            '-',
            '-',
            '-',
            "1x"+outputSize
        );

        return new MatrixSchema[] {
                new LayerSchema(1, outputSize)
        };
    }

    private <T extends MatrixSchema> int computeOutputSize(T[] channel) {
        int flatSize = 0;

        for (MatrixSchema filter : channel) {
            flatSize += filter.getColumnsCount() * filter.getRowsCount();
        }

        return flatSize;
    }
}



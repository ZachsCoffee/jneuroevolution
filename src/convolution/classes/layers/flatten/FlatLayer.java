package layers.flatten;

import layer.ConvolutionSchemaPrinter;
import layer.Layer;
import layer.MatrixReader;
import layer.MatrixSchema;
import maths.matrix.VectorReader;
import schema.LayerSchema;

public class FlatLayer implements Layer {

    @Override
    public MatrixReader[] computeLayer(MatrixReader[] channels) {
        double[] flat = new double[computeOutputSize(channels)];

        int startCopyIndex = 0;
        int rows, columns;
        for (MatrixReader filter : channels) {
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
    public MatrixSchema[] getSchema(MatrixSchema[] channels, ConvolutionSchemaPrinter convolutionSchemaPrinter) {
        int outputSize = computeOutputSize(channels);

        convolutionSchemaPrinter.addRow(
                "Flatten",
                channels.length,
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



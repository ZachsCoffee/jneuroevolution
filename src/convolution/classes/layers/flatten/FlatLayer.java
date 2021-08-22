package layers.flatten;

import layers.Layer;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixSchema;
import maths.matrix.VectorReader;
import schema.ConvolutionSchemaPrinter;
import schema.LayerSchema;

public class FlatLayer implements Layer {

    @Override
    public MatrixReader[] computeLayer(MatrixReader[] channel) {
        double[] flat = new double[computeOutputSize(channel)];

        int startCopyIndex = 0;
        for (MatrixReader filter: channel) {
            int rows = filter.getRowCount();
            int columns = filter.getColumnCount();
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
    public MatrixSchema[] getSchema(MatrixSchema[] channel, ConvolutionSchemaPrinter convolutionSchemaPrinter) {
        int outputSize = computeOutputSize((MatrixReader[]) channel);

        convolutionSchemaPrinter.addRow(
                "Flatten",
                channel.length,
                '-',
                '-',
                '-',
                outputSize
        );

        return new MatrixSchema[] {
                new LayerSchema(1, outputSize)
        };
    }

    private int computeOutputSize(MatrixReader[] channel) {
        int flatSize = 0;

        for (MatrixReader filter : channel) {
            flatSize += filter.getColumnCount() * filter.getRowCount();
        }

        return flatSize;
    }
}



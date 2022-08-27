package layers.flatten;

import core.layer.*;
import core.schema.SchemaRow;
import maths.matrix.VectorReader;
import core.schema.LayerSchema;

public class FlatLayer implements Layer, LayerSchemaResolver {

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
    public MatrixSchema[] getSchema(MatrixSchema[] inputChannels) {
        int outputSize = computeOutputSize(inputChannels);

        return new MatrixSchema[] {
                new LayerSchema(1, outputSize)
        };
    }

    @Override
    public SchemaRow getSchemaRow(MatrixSchema[] inputSchema) {
        int outputSize = computeOutputSize(inputSchema);

        return new SchemaRow()
            .setLayerType("Flatten")
            .setChannelsCount(inputSchema.length)
            .setOutput("1x"+outputSize);
    }

    @Override
    public Layer copy() {
        return new FlatLayer();
    }

    public int computeOutputSize(MatrixSchema[] channel) {
        int flatSize = 0;

        for (MatrixSchema filter : channel) {
            flatSize += filter.getColumnsCount() * filter.getRowsCount();
        }

        return flatSize;
    }
}



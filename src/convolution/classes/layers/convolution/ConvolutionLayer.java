package layers.convolution;

import filters.Filter;
import layers.Layer;
import schema.LayerSchema;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixReader2D;
import maths.matrix.MatrixSchema;
import maths.matrix.QubeSchema;
import schema.ConvolutionSchemaPrinter;
import schema.SchemaComputer;

import java.util.Objects;

public class ConvolutionLayer implements Layer {

    private final Filter[] filters;
    private final SchemaComputer schemaComputer;

    public ConvolutionLayer(Filter filter, int rows, int columns) {
        if (rows <= 0) throw new IllegalArgumentException(
                "Rows must be a positive number."
        );
        if (columns <= 0) throw new IllegalArgumentException(
                "Columns must be a positive number."
        );

        this.filters = new Filter[] {
                Objects.requireNonNull(filter)
        };

        schemaComputer = new SchemaComputer(new LayerSchema(rows, columns));
    }

    public ConvolutionLayer(Filter[] filters, int stride) {
        this(filters, stride, false);
    }

    public ConvolutionLayer(Filter[] filters, int stride, boolean keepSize) {
        this.filters = Objects.requireNonNull(filters);

        if (filters.length == 0) throw new RuntimeException(
                "Need at least one filter"
        );
        if (stride < 1) throw new IllegalArgumentException(
                "Stride can't be smaller than 1."
        );

        schemaComputer = new SchemaComputer(stride, keepSize);
    }

    public MatrixReader[] computeLayer(MatrixReader[] channels) {
        Objects.requireNonNull(channels);

        if (channels.length == 0) throw new IllegalArgumentException(
                "Need at least one matrix reader!"
        );

        MatrixReader[] output = new MatrixReader[filters.length * channels.length];

        int position;
        for (int i = 0; i< channels.length; i++) {
            for (int j=0; j<filters.length; j++) {
                position = i * channels.length + j;

                output[position] = computeForFilter(channels[i], filters[j]);
            }
        }

        return output;
    }

    @Override
    public MatrixSchema[] getSchema(MatrixSchema[] channels, ConvolutionSchemaPrinter convolutionSchemaPrinter) {
        MatrixSchema[] matrixSchemas = new MatrixSchema[channels.length * filters.length];
        for (int i = 0; i< channels.length; i++) {
            for (int j=0; j<filters.length; j++) {
                schemaComputer.compute(channels[i].getRowCount(), channels[i].getColumnCount(), filters[j].getKernelSize());

                matrixSchemas[i * filters.length + j] = new LayerSchema(schemaComputer.getRowsCount(), schemaComputer.getColumnsCount());
            }
        }

        convolutionSchemaPrinter.addRow(
                "Convolution",
                channels.length,
                filters.length,
                "-",
                schemaComputer.getStrideRows() + "x" + schemaComputer.getStrideColumns(),
                schemaComputer.getPaddingRows() + "x" + schemaComputer.getPaddingColumns(),
                channels.length + "x" + filters.length + "x" + schemaComputer.getRowsCount() + "x" + schemaComputer.getColumnsCount()
        );

        return matrixSchemas;
    }

    private MatrixReader computeForFilter(MatrixReader matrixReader, Filter filter) {
        Objects.requireNonNull(matrixReader);

        int inputRows = matrixReader.getRowCount();
        int inputColumns = matrixReader.getColumnCount();
        int kernelSize = filter.getKernelSize();

        schemaComputer.compute(inputRows, inputColumns, kernelSize);

        double[][] output = new double[schemaComputer.getRowsCount()][schemaComputer.getColumnsCount()];

        for (
                int i = - schemaComputer.getPaddingRows(), outI = 0;
                i < inputRows + schemaComputer.getPaddingRows() - kernelSize;
                i += schemaComputer.getStrideRows(), outI++
        ) {
            for (
                    int j = - schemaComputer.getPaddingColumns(), outJ = 0;
                    j < inputColumns + schemaComputer.getPaddingColumns() - kernelSize;
                    j += schemaComputer.getColumnsCount(), outJ++
            ) {
                output[outI][outJ] = filter.compute(i, j, matrixReader);
            }
        }

        return new MatrixReader2D(output);
    }
}

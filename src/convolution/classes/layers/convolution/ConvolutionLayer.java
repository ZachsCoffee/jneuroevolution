package layers.convolution;

import filters.Filter;
import layers.Layer;
import schema.LayerSchema;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixReader2D;
import maths.matrix.MatrixSchema;
import schema.ConvolutionSchemaPrinter;
import schema.SchemaComputer;
import utils.MatrixReaderUtils;

import java.util.Objects;

public class ConvolutionLayer implements Layer {

    private final Filter[] filters;
    private final SchemaComputer schemaComputer;
    private Boolean squashChannels = null;

    public ConvolutionLayer(Filter[] filters, int rows, int columns) {
        if (rows <= 0) throw new IllegalArgumentException(
            "Rows must be a positive number."
        );
        if (columns <= 0) throw new IllegalArgumentException(
            "Columns must be a positive number."
        );

//        this.filters = new Filter[] {
//                Objects.requireNonNull(filter)
//        };

        this.filters = Objects.requireNonNull(filters);

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

    public boolean isSquashChannels() {
        return squashChannels != null && squashChannels;
    }

    public ConvolutionLayer setSquashChannels(boolean squashChannels) {
        validateState(this.squashChannels);
        this.squashChannels = squashChannels;
        return this;
    }

    public MatrixReader[] computeLayer(MatrixReader[] channels) {
        Objects.requireNonNull(channels);

        if (channels.length == 0) throw new IllegalArgumentException(
            "Need at least one matrix reader!"
        );

        MatrixReader[] output;

        if (isSquashChannels()) {
            output = computeAsSquashedChannels(channels);
        }
        else {
            output = computeAsSeparateChannels(channels);
        }

        return output;
    }

    private MatrixReader[] computeAsSeparateChannels(MatrixReader[] channels) {
        MatrixReader[] output = new MatrixReader[filters.length * channels.length];

        for (int i = 0; i < channels.length; i++) {
            for (int j = 0; j < filters.length; j++) {
                int position = i * filters.length + j;

                output[position] = computeForFilter(channels[i], filters[j]);
            }
        }

        return output;
    }

    private MatrixReader[] computeAsSquashedChannels(MatrixReader[] channels) {
        MatrixReader[] output = new MatrixReader[filters.length];

        for (int i = 0; i < filters.length; i++) {
            MatrixReader squashedResults = computeForFilter(channels[0], filters[i]);
            for (int j = 1; j < channels.length; j++) {
                MatrixReader result = computeForFilter(channels[j], filters[i]);
                MatrixReaderUtils.squashAndAdd(squashedResults, result);
            }
            output[i] = squashedResults;
        }

        return output;
    }

    @Override
    public MatrixSchema[] getSchema(MatrixSchema[] channels, ConvolutionSchemaPrinter convolutionSchemaPrinter) {
        MatrixSchema[] matrixSchemas = new MatrixSchema[channels.length * filters.length];
        for (int i = 0; i < channels.length; i++) {
            for (int j = 0; j < filters.length; j++) {
                schemaComputer.compute(
                    channels[i].getRowsCount(),
                    channels[i].getColumnsCount(),
                    filters[j].getKernelSize()
                );

                matrixSchemas[i * filters.length + j] = new LayerSchema(
                    schemaComputer.getRowsCount(),
                    schemaComputer.getColumnsCount()
                );
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

        int inputRows = matrixReader.getRowsCount();
        int inputColumns = matrixReader.getColumnsCount();
        int kernelSize = filter.getKernelSize();

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
//                if (outI == output.length-1 || outJ == output[0].length -1) {
//                    System.out.println("ok");
//                }
//                    if (outI < output.length && outJ < output[0].length) {
                    output[outI][outJ] = filter.compute(i, j, matrixReader);
//                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new RuntimeException(
                "Input rows bound: " + inputColumnsBound + " input columns bound: " + inputColumnsBound,
                ex
            );
        }

        return new MatrixReader2D(output);
    }

    private void validateState(Object value) {
        if (value != null) {
            throw new IllegalStateException("Can't change the state.");
        }
    }
}

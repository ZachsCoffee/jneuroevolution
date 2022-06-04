package layers.convolution;

import filters.Filter;
import schema.LayerSchema;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixSchema;
import schema.ConvolutionSchemaPrinter;
import schema.SchemaComputer;
import utils.MatrixReaderUtils;

import java.util.Arrays;
import java.util.Objects;

public class ConvolutionLayer extends AbstractConvolutionLayer {

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

                output[position] = computeForKernel(channels[i], schemaComputer, filters[j]);
            }
        }

        return output;
    }

    private MatrixReader[] computeAsSquashedChannels(MatrixReader[] channels) {
        MatrixReader[] output = new MatrixReader[filters.length];

        for (int i = 0; i < filters.length; i++) {
            MatrixReader squashedResults = computeForKernel(channels[0], schemaComputer, filters[i]);
            for (int j = 1; j < channels.length; j++) {
                MatrixReader result = computeForKernel(channels[j], schemaComputer, filters[i]);
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

    private void validateState(Object value) {
        if (value != null) {
            throw new IllegalStateException("Can't change the state.");
        }
    }
}

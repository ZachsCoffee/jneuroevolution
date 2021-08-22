package schema;

import layers.convolution.ConvolutionUtils;

import java.util.Objects;

public class SchemaComputer {

    private static final int DEFAULT_STRIDE = 1;

    private final LayerSchema fixedSchema;
    private final boolean keepSize;
    private final int inputStride;

    private int paddingRows, paddingColumns;
    private int rowsCount, columnsCount;
    private int strideRows, strideColumns;

    public SchemaComputer(int stride, boolean keepSize) {
        if (stride < 1) throw new IllegalArgumentException(
                "Stride must be at least 1 not "+stride
        );

        this.inputStride = stride;
        this.keepSize = keepSize;
        fixedSchema = null;
    }

    public SchemaComputer(LayerSchema fixedSchema) {
        Objects.requireNonNull(fixedSchema);
        this.fixedSchema = fixedSchema;
        keepSize = false;

        this.inputStride = DEFAULT_STRIDE;
    }

    public void compute(int inputRows, int inputColumns, int kernelSize) {
        if (keepSize) {
            paddingRows = ConvolutionUtils.padding(inputRows, kernelSize, inputStride, inputRows);
            paddingColumns = ConvolutionUtils.padding(inputColumns, kernelSize, inputStride, inputColumns);

            rowsCount = ConvolutionUtils.outputDimension(inputRows, kernelSize, paddingRows, inputStride);
            columnsCount = ConvolutionUtils.outputDimension(inputColumns, kernelSize, paddingColumns, inputStride);

            // validate
            if (rowsCount != inputRows || columnsCount != inputColumns) throw new RuntimeException(
                    "Failed to keep the fixed size input rows "+inputRows+" input columns: "+inputColumns+" computed rows: "+rowsCount+" columns: "+columnsCount
            );
        }
        else if (fixedSchema != null) {
            if (inputRows > fixedSchema.getRowCount()) {
                paddingRows = 0;
                strideRows = ConvolutionUtils.stride(inputRows, kernelSize, 0, fixedSchema.getRowCount());
            }
            else {
                strideRows = DEFAULT_STRIDE;
                paddingRows = ConvolutionUtils.padding(inputRows, kernelSize, strideRows, fixedSchema.getRowCount());
            }

            if (inputColumns > fixedSchema.getColumnCount()) {
                paddingColumns = 0;
                strideColumns = ConvolutionUtils.stride(inputColumns, kernelSize, 0, fixedSchema.getColumnCount());
            }
            else {
                strideColumns = DEFAULT_STRIDE;
                paddingColumns = ConvolutionUtils.padding(inputColumns, kernelSize, strideColumns, fixedSchema.getColumnCount());
            }

            rowsCount = ConvolutionUtils.outputDimension(inputRows, kernelSize, paddingRows, strideRows);
            columnsCount = ConvolutionUtils.outputDimension(inputColumns, kernelSize, paddingColumns, strideColumns);

            // validate
            if (rowsCount != fixedSchema.getRowCount() || columnsCount != fixedSchema.getColumnCount()) throw new RuntimeException(
                    "Failed to keep the fixed size "+fixedSchema+" computed rows: "+rowsCount+" columns: "+columnsCount
            );
        }
        else {
            paddingRows = paddingColumns = 0;

            rowsCount = ConvolutionUtils.outputDimension(inputRows, kernelSize, paddingRows, inputStride);
            columnsCount = ConvolutionUtils.outputDimension(inputColumns, kernelSize, paddingColumns, inputStride);
        }
    }

    public int getPaddingRows() {
        return paddingRows;
    }

    public int getPaddingColumns() {
        return paddingColumns;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public int getColumnsCount() {
        return columnsCount;
    }

    public int getStrideRows() {
        return fixedSchema == null
            ? inputStride
            : strideRows;
    }

    public int getStrideColumns() {
        return fixedSchema == null
            ? inputStride
            : strideColumns;
    }
}

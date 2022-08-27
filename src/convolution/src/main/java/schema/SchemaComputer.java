package schema;

import core.schema.LayerSchema;
import layers.convolution.ConvolutionUtils;

import java.util.Objects;

public class SchemaComputer {

    private static final int DEFAULT_STRIDE = 1;

    private final LayerSchema fixedSchema;
    private final boolean keepSize;
    private final int strideRows, strideColumns;

    public SchemaComputer(int stride, boolean keepSize) {
        if (stride < 1) throw new IllegalArgumentException(
                "Stride must be at least 1 not "+stride
        );

        this.keepSize = keepSize;
        fixedSchema = null;

        strideRows = strideColumns = stride;
    }

    public SchemaComputer(LayerSchema fixedSchema) {
        Objects.requireNonNull(fixedSchema);
        this.fixedSchema = fixedSchema;
        keepSize = false;

        strideRows = strideColumns = DEFAULT_STRIDE;
    }

    public BluePrint compute(int inputRows, int inputColumns, int kernelSize) {
        BluePrint bluePrint = new BluePrint();

        bluePrint.strideRows = strideRows;
        bluePrint.strideColumns = strideColumns;

        if (keepSize) {
            bluePrint.paddingRows = ConvolutionUtils.padding(inputRows, kernelSize, bluePrint.strideRows, inputRows);
            bluePrint.paddingColumns = ConvolutionUtils.padding(inputColumns, kernelSize, bluePrint.strideColumns, inputColumns);

            bluePrint.rowsCount = ConvolutionUtils.outputDimension(inputRows, kernelSize, bluePrint.paddingRows, bluePrint.strideRows);
            bluePrint.columnsCount = ConvolutionUtils.outputDimension(inputColumns, kernelSize, bluePrint.paddingColumns, bluePrint.strideColumns);

            // validate
            if (bluePrint.rowsCount != inputRows || bluePrint.columnsCount != inputColumns) throw new RuntimeException(
                    "Failed to keep the fixed size input rows "+inputRows+" input columns: "+inputColumns+" computed rows: "+bluePrint.rowsCount+" columns: "+bluePrint.columnsCount
            );
        }
        else if (fixedSchema != null) {
            if (inputRows > fixedSchema.getRowsCount()) {
                bluePrint.paddingRows = 0;
                bluePrint.strideRows = ConvolutionUtils.stride(inputRows, kernelSize, 0, fixedSchema.getRowsCount());
            }
            else {
                bluePrint.strideRows = DEFAULT_STRIDE;
                bluePrint.paddingRows = ConvolutionUtils.padding(inputRows, kernelSize, bluePrint.strideRows, fixedSchema.getRowsCount());
            }

            if (inputColumns > fixedSchema.getColumnsCount()) {
                bluePrint.paddingColumns = 0;
                bluePrint.strideColumns = ConvolutionUtils.stride(inputColumns, kernelSize, 0, fixedSchema.getColumnsCount());
            }
            else {
                bluePrint.strideColumns = DEFAULT_STRIDE;
                bluePrint.paddingColumns = ConvolutionUtils.padding(inputColumns, kernelSize, strideColumns, fixedSchema.getColumnsCount());
            }

            bluePrint.rowsCount = ConvolutionUtils.outputDimension(inputRows, kernelSize, bluePrint.paddingRows, strideRows);
            bluePrint.columnsCount = ConvolutionUtils.outputDimension(inputColumns, kernelSize, bluePrint.paddingColumns, strideColumns);

            // validate
//            if (rowsCount != fixedSchema.getRowCount() || columnsCount != fixedSchema.getColumnCount()) throw new RuntimeException(
//                    "Failed to keep the fixed size "+fixedSchema+" computed rows: "+rowsCount+" columns: "+columnsCount
//            );
            if (bluePrint.rowsCount != fixedSchema.getRowsCount() || bluePrint.columnsCount != fixedSchema.getColumnsCount()) {
//                System.err.println(
//                        "Failed to keep the fixed size "+fixedSchema+" computed rows: "+rowsCount+" columns: "+columnsCount
//                );
                bluePrint.rowsCount = fixedSchema.getRowsCount();
                bluePrint.columnsCount = fixedSchema.getColumnsCount();
            }
        }
        else {
            bluePrint.paddingRows = bluePrint.paddingColumns = 0;

            bluePrint.rowsCount = ConvolutionUtils.outputDimension(inputRows, kernelSize, bluePrint.paddingRows, strideRows);
            bluePrint.columnsCount = ConvolutionUtils.outputDimension(inputColumns, kernelSize, bluePrint.paddingColumns, strideColumns);
        }

        return bluePrint;
    }
}

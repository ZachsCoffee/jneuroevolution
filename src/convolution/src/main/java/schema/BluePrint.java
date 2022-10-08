package schema;

public class BluePrint {

    int paddingRows, paddingColumns;
    int strideRows, strideColumns;
    int rowsCount, columnsCount;

    public BluePrint() {}

    public BluePrint(
        int paddingRows,
        int paddingColumns,
        int strideRows,
        int strideColumns,
        int rowsCount,
        int columnsCount
    ) {
        this.paddingRows = paddingRows;
        this.paddingColumns = paddingColumns;
        this.strideRows = strideRows;
        this.strideColumns = strideColumns;
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
    }

    public int getPaddingRows() {
        return paddingRows;
    }

    public int getPaddingColumns() {
        return paddingColumns;
    }

    public int getStrideRows() {
        return strideRows;
    }

    public int getStrideColumns() {
        return strideColumns;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public int getColumnsCount() {
        return columnsCount;
    }
}

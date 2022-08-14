package core.schema;

import core.layer.MatrixSchema;

public class LayerSchema implements MatrixSchema {

    private int rows, columns;

    public LayerSchema(int rows, int columns) {
        if (rows < 1) throw new RuntimeException(
                "Rows must be at least 1 given: "+rows
        );

        if (columns < 1) throw new RuntimeException(
                "Columns must be at least 1 given: "+columns
        );

        this.rows = rows;
        this.columns = columns;
    }

    private LayerSchema() {

    }

    @Override
    public int getRowsCount() {
        return rows;
    }

    @Override
    public int getColumnsCount() {
        return columns;
    }

    @Override
    public String toString() {
        return "rows(" + rows + ") columns(" + columns + ")";
    }
}

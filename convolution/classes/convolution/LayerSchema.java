package convolution;

import maths.matrix.MatrixSchema;

public class LayerSchema implements MatrixSchema {

    private final int rows, columns;

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

    @Override
    public int getRowCount() {
        return rows;
    }

    @Override
    public int getColumnCount() {
        return columns;
    }
}

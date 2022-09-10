package maths.matrix;

public class QubeSchema {
    private final int rows, columns, depth;

    public QubeSchema(int rows, int columns, int depth) {
        this.rows = rows;
        this.columns = columns;
        this.depth = depth;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "Depth: "+depth+" Rows: "+rows+" Columns: "+columns;
    }
}

package maths.matrix;

public interface MatrixRW extends MatrixReader, MatrixWriter{
    default void incrementBy(int rowIndex, int columnIndex, double value) {
        setValueAt(rowIndex, columnIndex, valueAt(rowIndex, columnIndex) + value);
    }

    default void incrementBy(int index, double value) {
        setValueAt(index / getRowsCount(), index % getColumnsCount(), value);
    }
}

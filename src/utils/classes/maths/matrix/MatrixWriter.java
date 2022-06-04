package maths.matrix;

public interface MatrixWriter extends MatrixSchema {
    void setValueAt(int rowIndex, int columnIndex, double value);

    default void setValueAt(int index, double value) {
        setValueAt(index / getRowsCount(), index % getColumnsCount(), value);
    }
}

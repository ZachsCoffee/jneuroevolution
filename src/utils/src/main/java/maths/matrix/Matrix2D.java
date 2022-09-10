package maths.matrix;

import java.util.Objects;

public class Matrix2D implements MatrixRW {

    private final double[][] data;

    public Matrix2D(double[][] data) {
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public int getRowsCount() {
        return data.length;
    }

    @Override
    public int getColumnsCount() {
        return data[0].length;
    }

    @Override
    public double valueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public double[] getRow(int position) {
        return data[position];
    }

    @Override
    public String toString() {
        return "Matrix: " + getRowsCount() + "x" + getColumnsCount();
    }

    @Override
    public void setValueAt(int rowIndex, int columnIndex, double value) {
        data[rowIndex][columnIndex] = value;
    }
}

package maths.matrix;

import maths.matrix.MatrixReader;

import java.util.Arrays;
import java.util.Objects;

public class MatrixReader2D implements MatrixReader {

    private final double[][] data;

    public MatrixReader2D(double[][] data) {
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
}

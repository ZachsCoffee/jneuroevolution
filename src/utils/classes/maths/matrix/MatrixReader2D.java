package maths.matrix;

import java.util.Objects;

public class MatrixReader2D implements MatrixReader {

    private final float[][] data;

    public MatrixReader2D(float[][] data) {
        this.data = Objects.requireNonNull(data);
    }
    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return data[0].length;
    }

    @Override
    public float valueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public float[] getRow(int position) {
        return data[position];
    }
}

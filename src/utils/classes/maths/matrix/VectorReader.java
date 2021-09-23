package maths.matrix;

import java.util.Objects;

public class VectorReader implements MatrixReader {

    private final float[] data;

    public VectorReader(float[] data) {
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public float valueAt(int rowIndex, int columnIndex) {
        return data[columnIndex];
    }

    @Override
    public float[] getRow(int position) {
        return data;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return data.length;
    }
}

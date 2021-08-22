package maths.matrix;

import java.util.Objects;

public class VectorReader implements MatrixReader {

    private final double[] data;

    public VectorReader(double[] data) {
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public double valueAt(int rowIndex, int columnIndex) {
        return data[columnIndex];
    }

    @Override
    public double[] getRow(int position) {
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

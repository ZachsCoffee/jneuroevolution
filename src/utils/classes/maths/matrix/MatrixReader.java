package maths.matrix;

public interface MatrixReader extends MatrixSchema {
    double valueAt(int rowIndex, int columnIndex);

    double[] getRow(int position);
}

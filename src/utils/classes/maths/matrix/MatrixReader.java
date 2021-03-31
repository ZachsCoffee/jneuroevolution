package maths.matrix;

public interface MatrixReader {
    int getRowCount();

    int getColumnCount();

    double valueAt(int rowIndex, int columnIndex);

    double[] getRow(int position);
}

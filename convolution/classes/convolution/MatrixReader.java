package convolution;

public interface MatrixReader {
    int getRowCount();

    int getColumnCount();

    double valueAt(int rowIndex, int columnIndex);
}

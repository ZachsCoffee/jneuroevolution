package maths.matrix;

public interface MatrixReader extends MatrixSchema {
    float valueAt(int rowIndex, int columnIndex);

    float[] getRow(int position);
}

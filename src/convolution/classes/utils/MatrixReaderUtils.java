package utils;

import maths.matrix.MatrixReader;
import maths.matrix.MatrixReader2D;

public class MatrixReaderUtils {
    private MatrixReaderUtils() {}

    public static MatrixReader add(MatrixReader first, MatrixReader second) {
        if (first.getRowsCount() != second.getRowsCount() || first.getColumnsCount() != second.getColumnsCount()) throw new IllegalArgumentException(
            "The two given matrix must have the same schema. First matrix: " + first + " second matrix: " + second
        );

        double[][] result = new double[first.getRowsCount()][first.getColumnsCount()];

        for (int i=0; i<first.getRowsCount(); i++) {
            for (int j=0; j<first.getColumnsCount(); j++) {
                result[i][j] = first.valueAt(i, j) + second.valueAt(i, j);
            }
        }

        return new MatrixReader2D(result);
    }

    public static void squashAndAdd(MatrixReader first, MatrixReader second) {
        if (first.getRowsCount() != second.getRowsCount() || first.getColumnsCount() != second.getColumnsCount()) throw new IllegalArgumentException(
            "The two given matrix must have the same schema. First matrix: " + first + " second matrix: " + second
        );

        for (int i=0; i<first.getRowsCount(); i++) {
            double[] row = first.getRow(i);
            for (int j=0; j<first.getColumnsCount(); j++) {
                row[j] += second.valueAt(i, j);
            }
        }
    }
}

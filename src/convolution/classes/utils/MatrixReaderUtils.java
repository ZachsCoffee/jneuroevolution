package utils;

import layer.MatrixReader;
import maths.matrix.MatrixRW;
import maths.matrix.Matrix2D;

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

        return new Matrix2D(result);
    }

    public static void squashAndAdd(MatrixReader squashDestination, MatrixReader data) {
        if (squashDestination.getRowsCount() != data.getRowsCount() || squashDestination.getColumnsCount() != data.getColumnsCount()) throw new IllegalArgumentException(
            "The two given matrix must have the same schema. First matrix: " + squashDestination + " data matrix: " + data
        );

        for (int i=0; i<squashDestination.getRowsCount(); i++) {
            double[] row = squashDestination.getRow(i);
            for (int j=0; j<squashDestination.getColumnsCount(); j++) {
                row[j] += data.valueAt(i, j);
            }
        }
    }

    public static void addConstant(MatrixRW squashDestination, double constant) {
        int index = 0;
        for (Double value : squashDestination) {
            squashDestination.incrementBy(index++, value + constant);
        }
    }
}

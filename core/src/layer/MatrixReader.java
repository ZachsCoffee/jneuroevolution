package layer;

import java.util.Iterator;

public interface MatrixReader extends MatrixSchema, Iterable<Double> {
    double valueAt(int rowIndex, int columnIndex);

    default double[] getRow(int position) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Iterator<Double> iterator() {
        return new MatrixReaderIterator(this);
    }
}

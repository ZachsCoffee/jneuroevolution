package layer;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class MatrixReaderIterator implements Iterator<Double> {

    private final MatrixReader matrixReader;
    private final int totalItems;
    private final int rowSize;
    private int index = 0;

    public MatrixReaderIterator(MatrixReader matrixReader) {
        this.matrixReader = Objects.requireNonNull(matrixReader);
        rowSize = matrixReader.getColumnsCount();
        totalItems = matrixReader.getRowsCount() * rowSize;
    }

    @Override
    public boolean hasNext() {
        return index < totalItems;
    }

    @Override
    public Double next() {
        int rowIndex = index / rowSize;
        int columnIndex = index % rowIndex;
        if (!hasNext()) {
            throw new NoSuchElementException("Failed to read position at rowIndex: " + rowIndex + " columnIndex: " + columnIndex);
        }

        index++;

        return matrixReader.valueAt(rowIndex, columnIndex);
    }
}

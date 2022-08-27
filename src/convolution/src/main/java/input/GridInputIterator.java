package input;

import core.layer.MatrixReader;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class GridInputIterator implements Iterator<ConvolutionInput> {

    private final int gridRows, gridColumns;
    private final int blockRows, blockColumns;
    private final GridBlock[] blockReaders;
    private final MatrixReader[] channels;
    private int blockIndex = 0;

    GridInputIterator(ConvolutionInput mainInput, int gridRows, int gridColumns, int blockRows, int blockColumns) {
        this.gridRows = gridRows;
        this.gridColumns = gridColumns;
        this.blockRows = blockRows;
        this.blockColumns = blockColumns;

        channels = mainInput.getChannels();
        blockReaders = new GridBlock[channels.length];
    }

    @Override
    public boolean hasNext() {
        return blockIndex < gridRows * gridColumns;
    }

    @Override
    public ConvolutionInput next() {
        if (! hasNext()) throw new NoSuchElementException(
                "I don't have more grid blocks."
        );

        for (int i = 0; i< blockReaders.length; i++) {
            blockReaders[i] = new GridBlock(channels[i], blockRows, blockColumns);
        }

        blockIndex++;

        return () -> blockReaders;
    }

    public class GridBlock implements MatrixReader {

        private final MatrixReader mainInput;
        private final int rowBound;
        private final int columnBound;
        private final int gridRowIndex;
        private final int gridColumnIndex;

        private GridBlock(MatrixReader mainInput, int rowBound, int columnBound) {
            this.mainInput = mainInput;
            this.rowBound = rowBound;
            this.columnBound = columnBound;

            gridRowIndex = blockIndex / gridColumns * rowBound;
            gridColumnIndex = blockIndex % gridColumns * columnBound;
        }

        @Override
        public double valueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= rowBound || columnIndex >= columnBound) throw new IndexOutOfBoundsException(
                    "Given rowIndex: "+rowIndex+" columnIndex: "+columnIndex+" row bound: "+rowBound+" column bound: "+columnBound
            );

            return mainInput.valueAt(gridRowIndex + rowIndex, gridColumnIndex + columnIndex);
        }

        @Override
        public double[] getRow(int position) {
            throw new UnsupportedOperationException("Can't get the row");
        }

        @Override
        public int getRowsCount() {
            return rowBound;
        }

        @Override
        public int getColumnsCount() {
            return columnBound;
        }

        public int getRealRow() {
            return gridRowIndex;
        }

        public int getRealColumn() {
            return gridColumnIndex;
        }
    }
}

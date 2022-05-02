package input;

import maths.matrix.MatrixReader;

import java.util.Iterator;
import java.util.Objects;

public class GridInput implements Iterable<ConvolutionInput> {

    private final ConvolutionInput input;
    private final int gridRows, gridColumns;
    private final int blockRows, blockColumns;
    private final int neighbords;

    public GridInput(ConvolutionInput input, int blockRows, int blockColumns) {
        this(input, blockRows, blockColumns, 0);
    }

    public GridInput(ConvolutionInput input, int blockRows, int blockColumns, int neighbors) {
        positive(blockRows);
        positive(blockColumns);

        if (neighbors < 0) throw new RuntimeException(
                "Neighbors can't be negative number"
        );

        this.neighbords = neighbors;

        sameChannelSchema(input);

        this.blockRows = blockRows + neighbors;
        this.blockColumns = blockColumns + neighbors;

        even(this.blockRows);
        even(this.blockColumns);

        int inputRows = input.getChannels()[0].getRowsCount();
        int inputColumns = input.getChannels()[0].getColumnsCount();
        even(inputRows);
        even(inputColumns);

        this.input = Objects.requireNonNull(input);
        gridRows = inputRows / this.blockRows;
        gridColumns = inputColumns / this.blockColumns;
    }

    private void sameChannelSchema(ConvolutionInput input) {
        MatrixReader[] channels = input.getChannels();
        if (channels.length == 0) throw new IllegalArgumentException(
                "Need at least one channel."
        );
        int firstChannelRows = channels[0].getRowsCount();
        int firstChannelColumns = channels[0].getColumnsCount();

        for (int i=1; i<channels.length; i++) {
            if (channels[i].getRowsCount() != firstChannelRows) throw new IllegalArgumentException(
                    "Not all channels have the same row count. First channel rows: "+firstChannelRows+" channel at "+i+" position, rows: "+channels[i].getRowsCount()
            );

            if (channels[i].getColumnsCount() != firstChannelColumns) throw new IllegalArgumentException(
                    "Not all channels have the same column count. First channel columns: "+firstChannelColumns+" channel at "+i+" position, columns: "+channels[i].getColumnsCount()
            );
        }
    }

    private void even(int number) {
        if (number == 0 || number % 2 != 0) throw new RuntimeException(
                "The number must be an even. Not even: '"+number+"'"
        );
    }

    private void positive(int number) {
        if (number < 1) throw new IllegalArgumentException(
                "Need a positive number. Not: '"+number+"'"
        );
    }

    @Override
    public Iterator<ConvolutionInput> iterator() {
        return new GridInputIterator(input, gridRows, gridColumns, blockRows, blockColumns);
    }
}

package input;

import core.layer.MatrixReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractImageInput implements MatrixReader {

    protected final BufferedImage bufferedImage;
    protected final float[] hsb = new float[3];
    protected final int realRowsCount, realColumnsCount;
    protected final int rowsCount, columnsCount;
    private final int[] rgbRow;

    public AbstractImageInput(BufferedImage bufferedImage) {
        this.bufferedImage = Objects.requireNonNull(bufferedImage);

        realRowsCount = bufferedImage.getHeight();
        realColumnsCount = bufferedImage.getWidth();

        if (realRowsCount % 2 != 0) {
            rowsCount = realRowsCount + 1;
        }
        else {
            rowsCount = realRowsCount;
        }

        if (realColumnsCount % 2 != 0) {
            columnsCount = realColumnsCount + 1;
        }
        else {
            columnsCount = realColumnsCount;
        }

        rgbRow = new int[realColumnsCount];
    }

    protected abstract double readValueOf(int rgb);

    @Override
    public double valueAt(int rowIndex, int columnIndex) {
        return readValueOf(bufferedImage.getRGB(columnIndex, rowIndex));
    }

    @Override
    public double[] getRow(int position) {
        bufferedImage.getRGB(0, position, rgbRow.length, 1, rgbRow, 0, 0);

        double[] data = new double[rgbRow.length];

        for (int i=0; i<data.length; i++) {
            data[i] = readValueOf(rgbRow[i]);
        }

        return data;
    }

    @Override
    public int getRowsCount() {
        return rowsCount;
    }

    @Override
    public int getColumnsCount() {
        return columnsCount;
    }

    protected float[] getHsb(int rowIndex, int columnIndex) {
        validatePosition(rowIndex, columnIndex);
        if (isFakePosition(rowIndex, columnIndex)) {
            Arrays.fill(hsb, 0);
            return hsb;
        }

        return getHsb(bufferedImage.getRGB(columnIndex, rowIndex));
    }

    protected float[] getHsb(int rgb) {
        Color.RGBtoHSB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, hsb);

        return hsb;
    }

    private boolean isFakePosition(int rowIndex, int columnIndex) {
        return rowIndex >= realRowsCount && rowIndex < rowsCount || columnIndex >= realColumnsCount && columnIndex < columnsCount;
    }

    private void validatePosition(int rowIndex, int columnIndex) {
        if (rowIndex >= rowsCount) throw new IndexOutOfBoundsException(
                "X " + rowIndex + " is out of bounds "+rowsCount
        );

        if (columnIndex >= columnsCount) throw new IndexOutOfBoundsException(
                "Y " + columnIndex + " is out of bounds "+columnsCount
        );
    }
}

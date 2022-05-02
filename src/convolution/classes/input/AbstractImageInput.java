package input;

import maths.matrix.MatrixReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractImageInput implements MatrixReader {

    protected final BufferedImage bufferedImage;
    protected final float[] hsb = new float[3];
    protected final int realRowsCount, realColumnsCount;
    protected final int rowsCount, columnsCount;

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
        int rgb = bufferedImage.getRGB(columnIndex, rowIndex);

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

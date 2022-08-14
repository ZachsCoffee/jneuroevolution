package utils;

import core.layer.MatrixReader;

import java.util.Objects;

public class MatrixUtils {

    /**
     * Merge all the given channels into one vector. The channels MUST be previously passed from a flatted convolution core.layer.
     *
     * @param channels
     * @return
     */
    public static double[] mergeChannels(MatrixReader[][] channels) {
        Objects.requireNonNull(channels);

        if (channels.length == 0) {
            throw new IllegalArgumentException("Need at least one channel.");
        }

        int channelsSize = 0;
        for (MatrixReader[] channel : channels) {
            if (channel.length == 0) {
                throw new IllegalArgumentException("Need at least one matrix per channel.");
            }

            int columnsCount = channel[0].getColumnsCount();
            if (columnsCount == 0) {
                throw new IllegalArgumentException("Need at least one value per channel matrix");
            }

            channelsSize += columnsCount;
        }

        double[] vector = new double[channels.length + channelsSize];

        double[] row;
        int offset = 0;
        for (MatrixReader[] channel : channels) {
            row = channel[0].getRow(0);
            System.arraycopy(row, 0, vector, offset, row.length);

            offset += row.length;
        }

        return vector;
    }

    private MatrixUtils() {
    }

}

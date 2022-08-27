package layers.convolution;

public class ConvolutionUtils {

    /**
     * Solve the formula for Stride.
     *
     * @param inputLength
     * @param sampleSize
     * @param padding
     * @param outputLength
     * @return The stride.
     */
    public static int stride(int inputLength, int sampleSize, int padding, int outputLength) {
        if (inputLength < 1) throw new IllegalArgumentException(
            "Need at least a inputLength of 1"
        );

        if (sampleSize < 1) throw new IllegalArgumentException(
            "Need at least a kernel size of 1"
        );

        if (padding < 0) throw new IllegalArgumentException(
            "Padding can't be a negative number"
        );

        if (outputLength < 2) throw new IllegalArgumentException(
            "Need at least a outputLength of 2"
        );

        return (inputLength - sampleSize + 2 * padding) / (outputLength - 1);
    }

    /**
     * Solve the formula for Padding.
     *
     * @param inputLength
     * @param sampleSize
     * @param stride
     * @param outputLength
     * @return The padding
     */
    public static int padding(int inputLength, int sampleSize, int stride, int outputLength) {
        if (inputLength < 1) throw new IllegalArgumentException(
            "Need at least a inputLength of 1"
        );

        if (sampleSize < 1) throw new IllegalArgumentException(
            "Need at least a kernel size of 1"
        );

        if (stride < 1) throw new IllegalArgumentException(
            "Stride can't be lower than 1"
        );

        return (outputLength * stride - inputLength + sampleSize - stride) / 2;
    }

    /**
     * Computes the output dimension based on the info (params).
     *
     * @param inputLength
     * @param sampleSize
     * @param padding
     * @param stride
     * @return The dimension size.
     */
    public static int outputDimension(int inputLength, int sampleSize, int padding, int stride) {
        if (inputLength < 1) throw new IllegalArgumentException(
            "Need at least a inputLength of 1"
        );

        if (sampleSize < 1) throw new IllegalArgumentException(
            "Need at least a kernel size of 1"
        );

        if (padding < 0) throw new IllegalArgumentException(
            "Padding can't be a negative number"
        );

        if (stride < 1) throw new IllegalArgumentException(
            "Stride can't be lower than 1"
        );

        return (inputLength - sampleSize + 2 * padding) / stride + 1;
    }

    /**
     * Computes both of output dimensions based on the info (params).
     *
     * @param inputRows
     * @param inputColumns
     * @param sampleSize
     * @param padding
     * @param stride
     * @return The first number is the rowsLength the second is the columnsLength. Or (Y, X)
     */
    public static int[] outputDimensions(int inputRows, int inputColumns, int sampleSize, int padding, int stride) {
        if (inputRows < 1 || inputColumns < 1) throw new IllegalArgumentException(
            "Need at least one inputRows and inputColumns given rows: " + inputRows + " columns: " + inputColumns
        );

        if (sampleSize < 1) throw new IllegalArgumentException(
            "Need at least a kernel size of 1 given: " + sampleSize
        );

        if (padding < 0) throw new IllegalArgumentException(
            "Padding can't be a negative number given: " + padding
        );

        if (stride < 1) throw new IllegalArgumentException(
            "Stride can't be lower than 1 given: " + stride
        );

        return new int[]{
            (inputRows - sampleSize + 2 * padding) / stride + 1,
            (inputColumns - sampleSize + 2 * padding) / stride + 1
        };
    }

    private ConvolutionUtils() {
    }
}

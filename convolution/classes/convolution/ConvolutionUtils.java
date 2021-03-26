package convolution;

public class ConvolutionUtils {
    private ConvolutionUtils() {}

    public static int padding(int inputLength, int kernelSize, int stride) {
        if (inputLength < 1) throw new IllegalArgumentException(
                "Need at least a inputLength of 1"
        );

        if (kernelSize < 1) throw new IllegalArgumentException(
                "Need at least a kernel size of 1"
        );

        if (stride < 1) throw new IllegalArgumentException(
                "Stride can't be lower than 1"
        );

        return (inputLength * stride - inputLength + kernelSize - stride) / 2;
    }

    public static int outputDimension(int inputLength, int kernelSize, int padding, int stride) {
        if (inputLength < 1) throw new IllegalArgumentException(
                "Need at least a inputLength of 1"
        );

        if (kernelSize < 1) throw new IllegalArgumentException(
                "Need at least a kernel size of 1"
        );

        if (padding < 0) throw new IllegalArgumentException(
                "Padding can't be a negative number"
        );

        if (stride < 1) throw new IllegalArgumentException(
                "Stride can't be lower than 1"
        );

        return (inputLength - kernelSize + 2 * padding) / stride + 1;
    }

    public static int[] outputDimensions(int inputRows, int inputColumns, int sampleSize, int padding, int stride) {
        if (inputRows < 1 || inputColumns < 1) throw new IllegalArgumentException(
                "Need at least one inputRows and inputColumns given rows: "+inputRows+" columns: "+inputColumns
        );

        if (sampleSize < 1) throw new IllegalArgumentException(
                "Need at least a kernel size of 1 given: "+sampleSize
        );

        if (padding < 0) throw new IllegalArgumentException(
                "Padding can't be a negative number given: "+padding
        );

        if (stride < 1) throw new IllegalArgumentException(
                "Stride can't be lower than 1 given: "+stride
        );

        return new int[]{
                (inputRows - sampleSize + 2 * padding) / stride + 1,
                (inputColumns - sampleSize + 2 * padding) / stride + 1
        };
    }
}

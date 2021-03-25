package convolution;

import filters.Filter;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class ConvolutionLayer {
    private static final int MIN_SAMPLE_SIZE = 3;

    private final MatrixReader matrixReader;
    private final Filter[] filters;
    private final int inputRows, inputColumns;
    private final int stride;
    private final boolean keepSize;
    private final double[][][] output;

    public ConvolutionLayer(MatrixReader matrixReader, Filter[] filters, int stride, boolean keepSize) {
        this.matrixReader = Objects.requireNonNull(matrixReader);
        this.filters = Objects.requireNonNull(filters);

        if (MIN_SAMPLE_SIZE < 3) {
            throw new IllegalArgumentException("Sample size must be at least: "+MIN_SAMPLE_SIZE);
        }

        if (stride < 1) {
            throw new IllegalArgumentException("Stride can't be smaller than 1.");
        }

        this.stride = stride;
        this.keepSize = keepSize;

        if (filters.length == 0) {
            throw new IllegalArgumentException("Need at least one filter!");
        }

        // initial basic fields
        inputRows = matrixReader.getRowCount();
        inputColumns = matrixReader.getColumnCount();
        output = new double[filters.length][][];
    }

    public void computeLayer() {
        for (int i=0; i<filters.length; i++) {

            computeFilter(i);

            BufferedImage bufferedImage = new BufferedImage(output[i][0].length, output[i].length, BufferedImage.TYPE_INT_RGB);
            for (int p=0; p<output[i].length; p++) {
                for (int j=0; j<output[i][p].length; j++) {
                    int value = (int) (output[i][p][j] * 255);
                    bufferedImage.setRGB(j, p, (value << 8 | value << 16 | value));
                }
            }
        }
    }

    private void computeFilter(int filterIndex) {
        int kernelSize = filters[filterIndex].getKernelSize();
        int paddingRows = 0, paddingColumns = 0;
        if (keepSize) {
            paddingRows = computePadding(inputRows, kernelSize);
            paddingColumns = computePadding(inputColumns, kernelSize);
        }

        int outputRows = computeMatrixDimension(inputRows, kernelSize, paddingRows);
        int outputColumns = computeMatrixDimension(inputColumns, kernelSize, paddingColumns);

        output[filterIndex] = new double[outputRows][outputColumns];

        for (int i= - paddingRows, outI = 0; i < inputRows + paddingRows - kernelSize; i += stride, outI++) {
            for (int j= - paddingColumns, outJ = 0; j < inputColumns + paddingColumns - kernelSize; j += stride, outJ++) {
                output[filterIndex][outI][outJ] = filters[filterIndex].compute(i, j, matrixReader);
            }
        }
    }

    private int computePadding(int inputLength, int kernelSize) {
        return (inputLength * stride - inputLength + kernelSize - stride) / 2;
    }

    private int computeMatrixDimension(int dimensionLength, int kernelSize, int padding) {
        if (dimensionLength < 1) throw new IllegalArgumentException(
                "Need at least a dimensionLength of 1"
        );

        if (kernelSize < 1) throw new IllegalArgumentException(
                "Need at least a kernel size of 1"
        );

        if (padding < 0) throw new IllegalArgumentException(
                "Padding can't be a negative number"
        );

        return (dimensionLength - kernelSize + 2 * padding) / stride + 1;
    }
}

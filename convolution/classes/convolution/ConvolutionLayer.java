package convolution;

import filters.Filter;

import java.util.Objects;

public class ConvolutionLayer {
    private static final int MIN_SAMPLE_SIZE = 3;

    private final MatrixReader matrixReader;
    private final Filter[] filters;
    private final int stride;
    private final boolean keepSize;
    private final int sampleSize;

    public ConvolutionLayer(MatrixReader matrixReader, Filter[] filters, int sampleSize, int stride, boolean keepSize) {
        this.matrixReader = Objects.requireNonNull(matrixReader);
        this.filters = Objects.requireNonNull(filters);

        if (MIN_SAMPLE_SIZE < 3) {
            throw new IllegalArgumentException("Sample size must be at least: "+MIN_SAMPLE_SIZE);
        }

        int rowCount = matrixReader.getRowCount();
        int columnCount = matrixReader.getColumnCount();
        if (sampleSize > rowCount || sampleSize > columnCount) {
            throw new IllegalArgumentException(
                    "Sample size is bigger than the matrix reader: sample size "+sampleSize+" rowCount: "+rowCount+" columnCount"+columnCount
            );
        }

        if (stride < 1) {
            throw new IllegalArgumentException("Stride can't be smaller than 1.");
        }

        this.sampleSize = sampleSize;
        this.stride = stride;
        this.keepSize = keepSize;

        if (filters.length == 0) {
            throw new IllegalArgumentException("Need at least one filter!");
        }

        int inputRows = matrixReader.getRowCount();
        int inputColumns = matrixReader.getColumnCount();

    }

    public void compute() {
        int inputRows = matrixReader.getRowCount();
        int inputColumns = matrixReader.getColumnCount();

        // store the output of filters
        // create foreach loop for filters
        // TODO compute the padding for each filter
        int paddingRows, paddingColumns;
        if (keepSize) {
            paddingRows
        }


        for (int i=0; i + sampleSize < inputRows; i += stride) {
            for (int j=0; j + sampleSize < inputColumns; j += stride) {

            }
        }
    }

    public static void main(String[] args) {
        int imageH = 5;
        int kernelSize = 3;
        int stride = 2;

        System.out.println(;
    }

    private int computePadding(int inputLength, int kernelSize) {
        return (inputLength * stride - inputLength + kernelSize - stride) / 2;
    }
}

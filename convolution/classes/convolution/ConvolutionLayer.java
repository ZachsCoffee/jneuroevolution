package convolution;

import filters.Filter;
import filters.Kernel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConvolutionLayer {
    private static final int MIN_SAMPLE_SIZE = 3;

    private final MatrixReader matrixReader;
    private final Filter[] filters;
    private final int inputRows, inputColumns;
    private final int stride;
    private final boolean keepSize;
    private final int sampleSize;
    private final double[][][] output;

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

        // initial basic fields
        inputRows = matrixReader.getRowCount();
        inputColumns = matrixReader.getColumnCount();
        output = new double[filters.length][][];
    }

    public void computeLayer() {
        for (int i=0; i<filters.length; i++) {
            computeFilter(i);
            BufferedImage bufferedImage = new BufferedImage(output[i][0].length, output[i].length, BufferedImage.TYPE_INT_RGB);
            try {
                ImageIO.write(bufferedImage, "jpg", new File("output"+i));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void computeFilter(int filterIndex) {
        int paddingRows = 0, paddingColumns = 0;
        if (keepSize) {
            paddingRows = computePadding(inputRows, filters[filterIndex].getKernelSize());
            paddingColumns = computePadding(inputColumns, filters[filterIndex].getKernelSize());
        }

        int outputRows = computeMatrixDimension(inputRows, filters[filterIndex].getKernelSize(), paddingRows);
        int outputColumns = computeMatrixDimension(inputColumns, filters[filterIndex].getKernelSize(), paddingColumns);

        output[filterIndex] = new double[outputRows][outputColumns];
        int debug = outputColumns * outputColumns;
        int debugCount = 0;
        for (int i= - paddingRows, outI = 0; i + sampleSize < inputRows + paddingRows; i += stride, outI++) {
            for (int j= - paddingColumns, outJ = 0; j + sampleSize < inputColumns + paddingColumns; j += stride, outJ++) {
                output[filterIndex][outI][outJ] = filters[filterIndex].compute(i, j, matrixReader);
                debugCount++;
            }
        }

        if (debugCount != debug) {
            throw new RuntimeException("Output total:" + debug + " count: " + debugCount + " rows: "+ outputRows + " columns: "+outputColumns);
        }
    }

    public static void main(String[] args) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File("/home/zachs/Downloads/ΟΔ 5396.jpg"));

            MatrixReader matrixReader = new MatrixReader() {
                @Override
                public int getRowCount() {
                    return bufferedImage.getHeight();
                }

                @Override
                public int getColumnCount() {
                    return bufferedImage.getWidth();
                }

                @Override
                public double valueAt(int rowIndex, int columnIndex) {
                    int rgb = bufferedImage.getRGB(columnIndex, rowIndex);

                    return Color.RGBtoHSB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, null)[1];
                }
            };

            new ConvolutionLayer(matrixReader, new Filter[]{new Filter(Kernel.EDGE_DETECTION_HIGH)}, );

        } catch (IOException e) {
            e.printStackTrace();
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

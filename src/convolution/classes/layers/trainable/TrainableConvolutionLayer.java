package layers.trainable;

import filters.TrainableKernel;
import functions.ActivationFunction;
import layers.TrainableLayer;
import layers.convolution.AbstractConvolutionLayer;
import maths.Function;
import maths.matrix.MatrixRW;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixSchema;
import schema.ConvolutionSchemaPrinter;
import schema.LayerSchema;
import schema.SchemaComputer;
import utils.MatrixReaderUtils;

public class TrainableConvolutionLayer extends AbstractConvolutionLayer implements TrainableLayer {

    private static final int KERNEL_SIZE = 3;
    private static final Function FUNCTION = ActivationFunction.GROUND_RELU.getFunction();
    private final int kernelsPerChannel;
    private final boolean sumKernels;
    private final int stride;
    private final boolean keepSize;
    private final int channelsCount;
    private final int outputChannelsCount;
    private final MatrixReader[] output;
    private final double[] kernelsWeights;
    private final TrainableKernel[] kernels;
    private final int[] biasWeightIndexes;
    private SchemaComputer schemaComputer;

    public TrainableConvolutionLayer(
        int channelsCount,
        int kernelsPerChannel,
        boolean sumKernels,
        int stride,
        boolean keepSize
    ) {
        this.channelsCount = channelsCount;

        this.kernelsPerChannel = kernelsPerChannel;

        this.sumKernels = sumKernels;

        this.keepSize = keepSize;

        if (stride < 1) throw new IllegalArgumentException(
            "Stride can't be smaller than 1. Given: " + stride
        );

        this.stride = stride;

        if (sumKernels) {
            outputChannelsCount = kernelsPerChannel;
        }
        else {
            outputChannelsCount = channelsCount * kernelsPerChannel;
        }

        output = new MatrixReader[outputChannelsCount];

        kernels = createKernels();

        int totalWeightsCount = findTotalWeightsCount();

        kernelsWeights = new double[totalWeightsCount];
        biasWeightIndexes = createBiasWeightIndexes(totalWeightsCount);
    }

    @Override
    public int getWeightsCount() {
        return kernelsWeights.length;
    }

    @Override
    public int getOutputChannelsCount() {
        return outputChannelsCount;
    }

    @Override
    public void setWeightAt(int index, double weight) {
        kernelsWeights[index] = weight;
    }

    @Override
    public double getWeightAt(int index) {
        return kernelsWeights[index];
    }

    @Override
    public MatrixReader[] computeLayer(MatrixReader[] channels) {
        if (channels.length != channelsCount) throw new IllegalArgumentException(
            "The initial channels count is: " + this.channelsCount + " now the given is: " + channels.length
        );

        int kernelIndex = 0;
        if (sumKernels) {
            for (int i = 0; i < kernelsPerChannel; i++) {
                MatrixRW sumResult = computeForKernel(channels[0], schemaComputer, kernels[kernelIndex++]);
                for (int j = 1; j < channels.length; j++) {
                    MatrixReader kernelResult = computeForKernel(channels[j], schemaComputer, kernels[kernelIndex]);
                    MatrixReaderUtils.squashAndAdd(sumResult, kernelResult);
                    kernelIndex++;
                }
                MatrixReaderUtils.addConstant(sumResult, kernelsWeights[biasWeightIndexes[i]]);
                output[i] = sumResult;
            }
        }
        else {
            for (int i = 0; i < kernelsPerChannel; i++) {
                for (MatrixReader channel : channels) {
                    output[kernelIndex] = computeForKernel(channel, schemaComputer, kernels[kernelIndex]);
                    kernelIndex++;
                }
            }
        }

        return output;
    }

    @Override
    public MatrixSchema[] getSchema(
        MatrixSchema[] channels, ConvolutionSchemaPrinter convolutionSchemaPrinter
    ) {
        setupSchema();

        MatrixSchema[] matrixSchemas;
        if (sumKernels) {
            matrixSchemas = new MatrixSchema[kernelsPerChannel];
            for (int i = 0; i < kernelsPerChannel; i++) {
                schemaComputer.compute(
                    channels[i].getRowsCount(),
                    channels[i].getColumnsCount(),
                    KERNEL_SIZE
                );
                matrixSchemas[i] = new LayerSchema(
                    schemaComputer.getRowsCount(),
                    schemaComputer.getColumnsCount()
                );
            }
        }
        else {
            matrixSchemas = new MatrixSchema[channels.length * kernelsPerChannel];
            for (int i = 0; i < channels.length; i++) {
                for (int j = 0; j < kernelsPerChannel; j++) {
                    schemaComputer.compute(
                        channels[i].getRowsCount(),
                        channels[i].getColumnsCount(),
                        KERNEL_SIZE
                    );
                    matrixSchemas[i * kernelsPerChannel + j] = new LayerSchema(
                        schemaComputer.getRowsCount(),
                        schemaComputer.getColumnsCount()
                    );
                }
            }
        }

        return matrixSchemas;
    }

    @Override
    public TrainableLayer copy() {
        return new TrainableConvolutionLayer(
            channelsCount,
            kernelsPerChannel,
            sumKernels,
            stride,
            keepSize
        );
    }

    private void setupSchema() {
        schemaComputer = new SchemaComputer(stride, keepSize);
    }

    private int[] createBiasWeightIndexes(int totalWeightsCount) {
        if (sumKernels) {
            int[] biasWeightIndexes = new int[kernelsPerChannel];

            for (int i = 0; i < kernelsPerChannel; i++) {
                biasWeightIndexes[i] = i + totalWeightsCount;
            }

            return biasWeightIndexes;
        }

        return null;
    }

    private TrainableKernel[] createKernels() {
        TrainableKernel[] kernels = new TrainableKernel[channelsCount * kernelsPerChannel];

        int totalWeights = 0;
        int kernelIndex = 0;
        for (int i = 0; i < channelsCount; i++) {
            for (int j = 0; j < kernelsPerChannel; j++) {
                TrainableKernel kernel = new TrainableKernel(
                    totalWeights,
                    KERNEL_SIZE,
                    FUNCTION,
                    ! sumKernels
                );
                kernels[kernelIndex++] = kernel;

                totalWeights += kernel.getKernelTotalWeightsCount();
            }
        }

        return kernels;
    }

    private int findTotalWeightsCount() {
        int count = 0;
        for (TrainableKernel kernel : kernels) {
            count += kernel.getKernelTotalWeightsCount();
        }

        return count;
    }
}

package layers.trainable;

import core.layer.*;
import core.schema.SchemaRow;
import filters.TrainableKernel;
import functions.ActivationFunction;
import layers.convolution.AbstractConvolutionLayer;
import maths.Function;
import maths.matrix.MatrixRW;
import core.schema.LayerSchema;
import schema.BluePrint;
import schema.SchemaComputer;
import utils.MatrixReaderUtils;

import java.util.Arrays;

public class TrainableConvolutionLayer extends AbstractConvolutionLayer implements TrainableLayer, LayerSchemaResolver {

    private static final int KERNEL_SIZE = 5;
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
    private final SchemaComputer schemaComputer;

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
        Arrays.setAll(kernelsWeights, value -> Math.random() * 100);

        biasWeightIndexes = createBiasWeightIndexes(totalWeightsCount);

        schemaComputer = new SchemaComputer(stride, keepSize);

        for (TrainableKernel kernel : kernels) {
            kernel.setGlobalWeights(kernelsWeights);
        }
    }

    @Override
    public int getTotalWeights() {
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
    public MatrixReader[] execute(MatrixReader[] inputChannels) {
        if (inputChannels.length != channelsCount) throw new IllegalArgumentException(
            "The initial channels count is: " + this.channelsCount + " now the given is: " + inputChannels.length
        );

        int kernelIndex = 0;
        if (sumKernels) {
            for (int i = 0; i < kernelsPerChannel; i++) {
                MatrixRW sumResult = computeForKernel(inputChannels[0], schemaComputer, kernels[kernelIndex++]);
                for (int j = 1; j < inputChannels.length; j++) {
                    MatrixReader kernelResult = computeForKernel(inputChannels[j], schemaComputer, kernels[kernelIndex]);
                    MatrixReaderUtils.squashAndAdd(sumResult, kernelResult);
                    kernelIndex++;
                }
                MatrixReaderUtils.addConstant(sumResult, kernelsWeights[biasWeightIndexes[i]]);
                output[i] = sumResult;
            }
        }
        else {
            for (int i = 0; i < kernelsPerChannel; i++) {
                for (MatrixReader channel : inputChannels) {
                    output[kernelIndex] = computeForKernel(channel, schemaComputer, kernels[kernelIndex]);
                    kernelIndex++;
                }
            }
        }

        return output;
    }

    @Override
    public MatrixSchema[] getSchema(
        MatrixSchema[] inputChannels
    ) {
        BluePrint[] bluePrints = getBluePrints(inputChannels);

        MatrixSchema[] matrixSchemas = new MatrixSchema[bluePrints.length];

        for (int i=0; i<bluePrints.length; i++) {
            matrixSchemas[i] = new LayerSchema(
                bluePrints[i].getRowsCount(),
                bluePrints[i].getColumnsCount()
            );
        }

        return matrixSchemas;
    }

    public BluePrint[] getBluePrints(
        MatrixSchema[] inputChannels
    ) {
        BluePrint[] bluePrints;
        if (sumKernels) {
            validateInputChannels(inputChannels);

            bluePrints = new BluePrint[kernelsPerChannel];
            for (int i = 0; i < kernelsPerChannel; i++) {
                bluePrints[i] = schemaComputer.compute(
                    inputChannels[0].getRowsCount(),
                    inputChannels[0].getColumnsCount(),
                    KERNEL_SIZE
                );
            }
        }
        else {
            bluePrints = new BluePrint[inputChannels.length * kernelsPerChannel];
            for (int i = 0; i < inputChannels.length; i++) {
                for (int j = 0; j < kernelsPerChannel; j++) {
                    bluePrints[i * kernelsPerChannel + j] = schemaComputer.compute(
                        inputChannels[i].getRowsCount(),
                        inputChannels[i].getColumnsCount(),
                        KERNEL_SIZE
                    );
                }
            }
        }

        return bluePrints;
    }

    private void validateInputChannels(MatrixSchema[] inputChannels) {
        if (inputChannels.length == 0) throw new IllegalArgumentException(
            "Need at least on input channel."
        );

        int rows = inputChannels[0].getRowsCount();
        int columns = inputChannels[0].getColumnsCount();

        for (int i=1; i<inputChannels.length; i++) {
            if (rows != inputChannels[i].getRowsCount()) throw new IllegalArgumentException(
                "All the rows of the input channels must have the same size."
            );

            if (columns != inputChannels[i].getColumnsCount()) throw new IllegalArgumentException(
                "All the columns of the input channels must have the same size."
            );
        }
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

    @Override
    public SchemaRow getSchemaRow(MatrixSchema[] inputSchema) {
        BluePrint[] bluePrints = getBluePrints(inputSchema);
        BluePrint bluePrint = bluePrints[0];

        return new SchemaRow()
            .setLayerType("Trainable convolution")
            .setChannelsCount(inputSchema.length)
            .setFiltersCount(kernels.length)
            .setPadding(bluePrint.getPaddingRows()+"x"+bluePrint.getPaddingColumns())
            .setStride(bluePrint.getStrideRows()+"x"+bluePrint.getStrideColumns())
            .setOutput(bluePrints.length+"x"+bluePrint.getRowsCount()+"x"+bluePrint.getStrideRows());
    }

    private int[] createBiasWeightIndexes(int totalWeightsCount) {
        if (sumKernels) {
            int[] biasWeightIndexes = new int[kernelsPerChannel];

            for (int i = 0; i < kernelsPerChannel; i++) {
                biasWeightIndexes[i] = i + totalWeightsCount - kernelsPerChannel;
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
        int biasWeight = sumKernels
            ? 1
            : 0;

        int count = 0;
        for (TrainableKernel kernel : kernels) {
            count += kernel.getKernelTotalWeightsCount() + biasWeight;
        }

        return count;
    }
}

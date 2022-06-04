package layers.trainable;

import filters.TrainableKernel;
import functions.ActivationFunction;
import layers.convolution.AbstractConvolutionLayer;
import maths.Function;
import maths.matrix.MatrixRW;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixSchema;
import options.Lockable;
import schema.ConvolutionSchemaPrinter;
import schema.LayerSchema;
import schema.SchemaComputer;
import utils.MatrixReaderUtils;

public class TrainableConvolutionLayer extends AbstractConvolutionLayer {

    private static final int KERNEL_SIZE = 3;
    private static final Function FUNCTION = ActivationFunction.GROUND_RELU.getFunction();
    private final Options options;
    private SchemaComputer schemaComputer;
    private double[] kernelsWeights;
    private TrainableKernel[] kernels;
    private int channelsCount;
    private boolean setupLayer = true;
    private int[] biasWeightIndexes;

    public TrainableConvolutionLayer() {
        this(new Options());
    }

    public TrainableConvolutionLayer(Options options) {
        this.options = options == null
            ? new Options()
            : options;
    }

    @Override
    public MatrixReader[] computeLayer(MatrixReader[] channels) {
        if (setupLayer) {
            setupLayer(channels);
            channelsCount = channels.length;
            setupLayer = false;
        }
        else {
            if (channels.length != channelsCount) throw new IllegalArgumentException(
                "The initial channels count is: " + this.channelsCount + " now the given is: " + channels.length
            );
        }

        MatrixReader[] outcome;
        if (options.sumKernels) {
            outcome = new MatrixReader[options.kernelsPerChannel];
            int kernelIndex = 0;
            for (int i = 0; i < options.kernelsPerChannel; i++) {
                MatrixRW sumResult = computeForKernel(channels[0], schemaComputer, kernels[kernelIndex++]);
                for (int j=1; j<channels.length; j++) {
                    MatrixReader kernelResult = computeForKernel(channels[j], schemaComputer, kernels[kernelIndex]);
                    MatrixReaderUtils.squashAndAdd(sumResult, kernelResult);
                    kernelIndex++;
                }
                MatrixReaderUtils.addConstant(sumResult, kernelsWeights[biasWeightIndexes[i]]);
                outcome[i] = sumResult;
            }
        }
        else {
            outcome = new MatrixReader[channels.length * options.kernelsPerChannel];

            int kernelIndex = 0;
            for (int i = 0; i < options.kernelsPerChannel; i++) {
                for (MatrixReader channel : channels) {
                    outcome[kernelIndex] = computeForKernel(channel, schemaComputer, kernels[kernelIndex]);
                    kernelIndex++;
                }
            }
        }

        return outcome;
    }

    @Override
    public MatrixSchema[] getSchema(
        MatrixSchema[] channels, ConvolutionSchemaPrinter convolutionSchemaPrinter
    ) {
        setupSchema();

        MatrixSchema[] matrixSchemas;
        if (options.sumKernels) {
            matrixSchemas = new MatrixSchema[options.kernelsPerChannel];
            for (int i=0; i<options.kernelsPerChannel; i++) {
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
            matrixSchemas = new MatrixSchema[channels.length * options.kernelsPerChannel];
            for (int i=0; i<channels.length; i++) {
                for (int j=0; j<options.kernelsPerChannel; j++) {
                    schemaComputer.compute(
                        channels[i].getRowsCount(),
                        channels[i].getColumnsCount(),
                        KERNEL_SIZE
                    );
                    matrixSchemas[i * options.kernelsPerChannel + j] = new LayerSchema(
                        schemaComputer.getRowsCount(),
                        schemaComputer.getColumnsCount()
                    );
                }
            }
        }

        return matrixSchemas;
    }

    private void setupLayer(MatrixReader[] channels) {
        options.lock();
        setupKernels(channels);

        setupSchema();
    }

    private void setupSchema() {
        options.lock();
        schemaComputer = new SchemaComputer(options.stride, options.keepSize);
    }

    private void setupKernels(MatrixReader[] channels) {
        kernels = new TrainableKernel[channels.length * options.kernelsPerChannel];

        int totalWeights = 0;
        int kernelIndex = 0;
        for (int i = 0; i < channels.length; i++) {
            for (int j = 0; j < options.kernelsPerChannel; j++) {
                TrainableKernel kernel = new TrainableKernel(
                    totalWeights,
                    KERNEL_SIZE,
                    FUNCTION,
                    ! options.sumKernels
                );
                kernels[kernelIndex++] = kernel;

                totalWeights += kernel.getKernelTotalWeightsCount();
            }
        }

        if (options.sumKernels) {
            int startWeightsIndex = totalWeights;
            totalWeights += options.kernelsPerChannel;
            biasWeightIndexes = new int[options.kernelsPerChannel];

            for (int i=0; i<options.kernelsPerChannel; i++) {
                biasWeightIndexes[i] = i + startWeightsIndex;
            }
        }

        kernelsWeights = new double[totalWeights];
    }

    public static class Options extends Lockable {

        private int kernelsPerChannel = 1;
        private boolean sumKernels = false;
        private int stride = 1;
        private boolean keepSize = false;

        public int getKernelsPerChannel() {
            return kernelsPerChannel;
        }

        public Options setKernelsPerChannel(int kernelsPerChannel) {
            checkLock();
            if (kernelsPerChannel < 1) throw new IllegalArgumentException(
                "Kernels per channel can't be less than one."
            );

            this.kernelsPerChannel = kernelsPerChannel;
            return this;
        }

        public boolean isSumKernels() {
            return sumKernels;
        }

        public Options setSumKernels(boolean sumKernels) {
            checkLock();
            this.sumKernels = sumKernels;
            return this;
        }

        public int getStride() {
            return stride;
        }

        public Options setStride(int stride) {
            checkLock();
            if (stride < 1) throw new IllegalArgumentException(
                "Stride can't be smaller than 1. Given: " + stride
            );

            this.stride = stride;
            return this;
        }

        public boolean isKeepSize() {
            return keepSize;
        }

        public Options setKeepSize(boolean keepSize) {
            checkLock();
            this.keepSize = keepSize;
            return this;
        }
    }
}

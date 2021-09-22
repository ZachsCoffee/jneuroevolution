package networks.multilayer_perceptron;

import networks.gpu.GpuProgram;
import org.jocl.cl_mem;

import java.util.Arrays;
import java.util.Objects;

public class FastNetwork extends NeuralNetwork {
    private final float[] allOutputs;
    private final GpuProgram gpuProgram;
    private cl_mem weightsPointer, outputsPointer;

    public FastNetwork(GpuProgram gpuProgram, NetworkLayer[] layers) {
        super(layers);
        this.gpuProgram = Objects.requireNonNull(gpuProgram);

        int count = 0;
        for (int i=0; i<layers.length; i++) {
            count += layers[i].getNeuronsCount();
        }

        allOutputs = new float[count + layers[0].getLayerInputCount()];
        weightsPointer = gpuProgram.allocateBuffer(weights, GpuProgram.AllocationType.READ);
    }

    @Override
    public float[] compute(float[] features) {

        System.arraycopy(features, 0, allOutputs, 0, features.length);
        outputsPointer = gpuProgram.allocateBuffer(allOutputs, GpuProgram.AllocationType.WRITE);

        int weightsOffset = 0;
        int outputOffset = features.length;
        int lastOutputOffset = 0;
        int lastOutputNeurons = features.length;
        for (int i=0; i<layers.length; i++) {
            gpuProgram
                    .addArgument(weightsOffset)
                    .addArgument(outputOffset)
                    .addArgument(layers[i].NUMBER_OR_WEIGHTS)
                    .addArgument(weights.length)
                    .addArgument(lastOutputOffset)
                    .addArgument(lastOutputNeurons)
                    .addArgument(weightsPointer)
                    .addArgument(outputsPointer)
                    .executeNDRangeKernel(weights.length);

            lastOutputOffset += layers[i].getNeuronsCount();
            lastOutputNeurons = layers[i].getNeuronsCount();
            weightsOffset += layers[i].getNeuronsCount() * layers[i].NUMBER_OR_WEIGHTS;
            outputOffset += layers[i].getNeuronsCount();
        }

        gpuProgram.deallocateBuffer(outputsPointer);

        return super.compute(features);
    }

    public static void main(String[] args) {

    }
}

package networks.multilayer_perceptron;

import networks.gpu.GpuBufferAllocation;
import networks.gpu.GpuProgram;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Objects;

public class FastNetwork extends NeuralNetwork {
    private final float[] allOutputs;
    private final GpuProgram gpuProgram;
    private GpuBufferAllocation weightsPointer, outputsPointer;

    public FastNetwork(GpuProgram gpuProgram, NetworkLayer[] layers) {
        super(layers);
        this.gpuProgram = Objects.requireNonNull(gpuProgram);

        int count = 0;
        for (int i=0; i<layers.length; i++) {
            count += layers[i].getNeuronsCount();
        }

        allOutputs = new float[count + layers[0].getLayerInputCount()];
        weightsPointer = gpuProgram.allocateBuffer(weights, GpuProgram.AllocationType.READ);
//        outputsPointer = gpuProgram.allocatePinned(allOutputs, GpuProgram.AllocationType.READ_WRITE);
    }

    @Override
    public float[] compute(float[] features) {
        long a = System.currentTimeMillis();

        System.arraycopy(features, 0, allOutputs, 0, features.length);
        outputsPointer = gpuProgram.allocatePinned(allOutputs, GpuProgram.AllocationType.READ_WRITE);
        outputsPointer.getPinnedMemory().order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().put(allOutputs);

        int weightsOffset = 0;
        int outputOffset = features.length;
        int lastOutputOffset = 0;
        int lastOutputNeurons = features.length;
        for (int i=0; i<layers.length; i++) {
            gpuProgram
                    .addArgument(weightsOffset)
                    .addArgument(lastOutputNeurons)
                    .addArgument(lastOutputOffset)
                    .addArgumentFloatArray(layers[i].NUMBER_OR_WEIGHTS)
                    .addArgument(weightsPointer.getGpuMemory())
                    .addArgument(outputsPointer.getGpuMemory())
                    .executeNDRangeKernel(layers[i].getNeuronsCount() * layers[i].NUMBER_OR_WEIGHTS, layers[i].NUMBER_OR_WEIGHTS);

            lastOutputOffset += lastOutputNeurons;
            lastOutputNeurons = layers[i].getNeuronsCount();
            weightsOffset += layers[i].getNeuronsCount() * layers[i].NUMBER_OR_WEIGHTS;
            outputOffset += layers[i].getNeuronsCount();
        }
        gpuProgram.finish();


//        outputsPointer = gpuProgram.allocateBufferOverwrite(GpuProgram.AllocationType.READ_MAPPED, outputsPointer, allOutputs.length);
//        gpuProgram.getFloatBufferPinned(outputsPointer);
        System.out.println(System.currentTimeMillis() - a);

        FloatBuffer floatBuffer = outputsPointer.getPinnedMemory().order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
        float[] result = new float[layers[layers.length -1].getNeuronsCount()];
        floatBuffer.get(allOutputs);
        System.out.println(Arrays.toString(allOutputs));
        System.arraycopy(allOutputs, allOutputs.length - result.length, result, 0, result.length);

        gpuProgram.deallocateBuffer(outputsPointer);

//        a = System.currentTimeMillis();

//        super.compute(features);
//        System.out.println(System.currentTimeMillis() - a);
        return result;
    }

    public static void main(String[] args) {
        FastNetwork neuralNetwork = new FastNetwork(
                GpuLayerProgram.gpuProgram,
                new NetworkLayer[] {
                        new NetworkLayer(5, 5),
                        new NetworkLayer(2, 5),
                        new NetworkLayer(1, 2),
                        new NetworkLayer(1, 1),
                }
        );

        float[] result = neuralNetwork.compute(new float[] {
                1,2,3,4,5
        });

        System.out.println(Arrays.toString(result));
    }
}

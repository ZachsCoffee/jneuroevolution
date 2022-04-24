package networks.multilayer_perceptron;

import com.amd.aparapi.Range;

import com.amd.aparapi.device.Device;
import com.amd.aparapi.device.OpenCLDevice;
import networks.gpu.Kernel;
import networks.gpu.NetworkLayerKernel;


import java.util.Arrays;

public class FastNetwork extends NeuralNetwork {
    private final float[] allOutputs;
    private final NetworkLayerKernel networkLayerKernel;

    public FastNetwork(NetworkLayer[] layers) {
        super(layers);

        int count = 0;
        for (int i=0; i<layers.length; i++) {
            count += layers[i].getNeuronsCount();
        }

        allOutputs = new float[count + layers[0].getLayerInputCount()];
        networkLayerKernel = new NetworkLayerKernel(weights, allOutputs);
    }

    @Override
    public float[] compute(float[] features) {
        long a = System.currentTimeMillis();
//        Device.firstGPU(Kernel.class);
        int weightsOffset = 0;
        int outputOffset = features.length;
        int lastOutputOffset = 0;
        int lastOutputNeurons = features.length;
        for (int i=0; i<layers.length; i++) {
            networkLayerKernel
                    .setWeightOffset(weightsOffset)
                    .setLastOutputOffset(lastOutputNeurons)
                    .setLastOutputOffset(lastOutputOffset)
                    .setProductCache(new float[layers[i].NUMBER_OR_WEIGHTS]);
            networkLayerKernel
                    .setExplicit(true);
            networkLayerKernel
                    .execute(Range.create(layers[i].getNeuronsCount() * layers[i].NUMBER_OR_WEIGHTS, layers[i].NUMBER_OR_WEIGHTS));

            lastOutputOffset += lastOutputNeurons;
            lastOutputNeurons = layers[i].getNeuronsCount();
            weightsOffset += layers[i].getNeuronsCount() * layers[i].NUMBER_OR_WEIGHTS;
            outputOffset += layers[i].getNeuronsCount();
        }
//        outputsPointer = gpuProgram.allocateBufferOverwrite(GpuProgram.AllocationType.READ_MAPPED, outputsPointer, allOutputs.length);
//        gpuProgram.getFloatBufferPinned(outputsPointer);
        System.out.println(System.currentTimeMillis() - a);
        networkLayerKernel.get(allOutputs);
        float[] result = new float[layers[layers.length -1].getNeuronsCount()];
        System.arraycopy(networkLayerKernel.getOutput(), networkLayerKernel.getOutput().length - result.length, result, 0, result.length);

        return result;
    }

    public static void main(String[] args) {
        FastNetwork neuralNetwork = new FastNetwork(
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

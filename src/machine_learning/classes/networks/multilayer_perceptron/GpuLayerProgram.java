package networks.multilayer_perceptron;

import networks.gpu.GpuProgram;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class GpuLayerProgram {
    public static GpuProgram gpuProgram;

    static {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(GpuLayerProgram.class.getResource("kernel2.cl").getFile()));
            String kernelSourceCode = new String(bytes, StandardCharsets.UTF_8);

            gpuProgram = new GpuProgram(
                    "networkKernel", kernelSourceCode
            );
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        float[] features = new float[] {1,2,3,4,5};

        int weightsOffset = 0;
        int outputOffset = features.length;
        int lastOutputOffset = 0;
        int lastOutputNeurons = features.length;
        int neuronWeights = 6;
        float[] weights = new float[30];
        float[] output = new float[11];
        Arrays.fill(weights, 1);

        System.arraycopy(features, 0, output, 0, features.length);

        for (int i=0; i<weights.length; i++) {
            int gid = i;
            int outputIndex = (gid - weightsOffset) / neuronWeights + outputOffset;

            if ((gid + weightsOffset) % neuronWeights == neuronWeights - 1) {
                output[outputIndex] += weights[gid + weightsOffset];
            }
            else {
                output[outputIndex] += weights[gid + weightsOffset] * output[(outputIndex - lastOutputOffset + gid) % lastOutputNeurons];
            }
        }

    }
}

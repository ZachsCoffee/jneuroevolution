package networks.multilayer_perceptron;

import networks.gpu.GpuProgram;

public class GpuLayerProgram {
    public static GpuProgram gpuProgram;

    static {
        gpuProgram = new GpuProgram(
                "fastLayer",
                "__kernel void fastLayer(\n" +
                        "    __global const int neuronWeights,\n" +
                        "    __global const int weightsCount,\n" +
                        "    __global const float *input,\n" +
                        "    __global const float *weights,\n" +
                        "    __global float *output\n" +
                        ") {\n" +
                        "    int gid = get_global_id(0);\n" +
                        "\n" +
                        "    if (gid == weightsCount - 2) {\n" +
                        "        output[gid / neuronWeights] += weights[gid];\n" +
                        "    }\n" +
                        "    else {\n" +
                        "        output[gid / neuronWeights] += weights[gid] * input[gid];\n" +
                        "    }\n" +
                        "}"
        );
    }
}

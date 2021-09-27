__kernel void fastLayer(
    __const const int groupIndex,
    __const const int weightsOffset,
    __const const int outputOffset,
    __const const int neuronWeights,
    __const const int lastOutputOffset,
    __const const int lastOutputNeurons,
    __global const float *weights,
    __global float *output
) {
    // if (get_global_id(0) == 2) {
    //     for (int i=0; i<get_global_size(0); i++) {
    //         output[i] = output[1];
    //     }
    // }
    int weightIndex = groupIndex + (neuronWeights * get_global_id(0)) + weightsOffset;
    int outputIndex = (weightIndex - weightsOffset) / neuronWeights + outputOffset;
    if ((weightIndex + weightsOffset) % neuronWeights == neuronWeights - 1) {
        output[outputIndex] += weights[weightIndex];
    }
    else {
        output[outputIndex] += weights[weightIndex] * output[lastOutputOffset + groupIndex];
    }
}

// barrier(CLK_GLOBAL_MEM_FENCE);

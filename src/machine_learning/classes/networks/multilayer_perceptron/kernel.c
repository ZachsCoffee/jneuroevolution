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
    int weightIndex = groupIndex + (neuronWeights * get_global_id(0)) + weightsOffset;
    int outputIndex = (weightIndex - weightsOffset) / neuronWeights + outputOffset;
// barrier(CLK_GLOBAL_MEM_FENCE);
// barrier(CLK_GLOBAL_MEM_FENCE);
    if ((weightIndex + weightsOffset) % neuronWeights == neuronWeights - 1) {
        // atomicWrite(output, weights[gid + weightsOffset], outputIndex);
        output[outputIndex] += weights[weightIndex];
    }
    else {
        // atomicWrite(output, weights[gid + weightsOffset] * output[(outputIndex - lastOutputOffset + gid) % lastOutputNeurons], outputIndex);
        output[outputIndex] += weights[weightIndex] * output[lastOutputOffset + groupIndex];
    }
// barrier(CLK_GLOBAL_MEM_FENCE);
    // output[outputIndex] = get_local_size(0);
}
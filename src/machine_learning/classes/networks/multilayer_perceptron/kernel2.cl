// local size prepei na einai to input + 1 ara kai to product cache to idio
__kernel void networkKernel(
    __private const uint weightOffset,
    __private const uint lastNeuronsCount,
    __private const uint lastOutputOffset,
    __local float *productCache,
    __global const float *layerWeights,
    __global float *output
) {
    size_t groupIndex = get_group_id(0);
    size_t localSize = get_local_size(0);
    size_t localIndex = get_local_id(0);

    if (localIndex == localSize - 1) {
        productCache[localIndex] = layerWeights[weightOffset + localIndex];
    }
    else {
        productCache[localIndex] = layerWeights[weightOffset + localIndex] * output[lastOutputOffset + localIndex];
    }

    barrier(CLK_LOCAL_MEM_FENCE);

    // start to sum the products

    size_t pivot = localSize;
    size_t lastPivot = localSize;

    if (localIndex >= pivot) {
        return;
    }

    float sum;
    size_t second;
    do {
        pivot = (size_t) ceil(pivot / 2.0f);

        sum = productCache[localIndex];
        second = pivot + localIndex;
        if (second < lastPivot) {
            sum += productCache[second];
        }

        productCache[localIndex] = sum;

        lastPivot = pivot;
        barrier(CLK_LOCAL_MEM_FENCE);
    } while (pivot > 1);

    output[lastOutputOffset + lastNeuronsCount + groupIndex] = productCache[0];
}
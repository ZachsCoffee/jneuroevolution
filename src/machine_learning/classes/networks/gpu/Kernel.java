package networks.gpu;

import com.amd.aparapi.Range;
import com.amd.aparapi.opencl.OpenCL;
import networks.multilayer_perceptron.GpuLayerProgram;

import java.nio.file.Paths;

@OpenCL.Resource("kernel2.cl")
public interface Kernel extends OpenCL<NetworkLayerKernel> {
    NetworkLayerKernel compute(
            Range range,
            @Constant("weightOffset") final int weightOffset,
            @Constant("lastNeuronsCount") final int lastNeuronsCount,
            @Constant("lstOutputOffset") final int lastOutputOffset,
            @Local("productCache") float[] productCache,
            @GlobalReadOnly("layerWeights") float[] layerWeights,
            @GlobalReadWrite("output") float[] output
    );
}

package networks.gpu;

import org.jocl.Pointer;
import org.jocl.cl_mem;

import java.nio.ByteBuffer;

public class GpuBufferAllocation {
    cl_mem gpuMemory;
    Pointer pointer;
    ByteBuffer pinnedMemory;

    GpuBufferAllocation() {

    }

    GpuBufferAllocation(cl_mem gpuMemory, Pointer pointer) {
        this.gpuMemory = gpuMemory;
        this.pointer = pointer;
    }

    public cl_mem getGpuMemory() {
        return gpuMemory;
    }

    public Pointer getPointer() {
        return pointer;
    }

    public ByteBuffer getPinnedMemory() {
        return pinnedMemory;
    }
}

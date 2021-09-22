package networks.gpu;

import org.jocl.Pointer;
import org.jocl.cl_mem;

public class GpuBufferAllocation {
    cl_mem gpgMemory;
    Pointer pointer;

    GpuBufferAllocation() {

    }

    GpuBufferAllocation(cl_mem gpgMemory, Pointer pointer) {
        this.gpgMemory = gpgMemory;
        this.pointer = pointer;
    }

    public cl_mem getGpuMemory() {
        return gpgMemory;
    }

    public Pointer getPointer() {
        return pointer;
    }
}

package networks.gpu;

import org.jocl.*;

import java.util.LinkedList;
import java.util.List;

import static org.jocl.CL.*;

public class GpuProgram {

    private final cl_program program;
    private final cl_kernel kernel;
    private final List<GpuBufferAllocation> allocations = new LinkedList<>();
    private cl_command_queue commandQueue;
    private int argumentIndex = 0;

    public GpuProgram(String programName, String sourceCode) {
        createCommandQueue();

        program = clCreateProgramWithSource(
                Gpu.getInstance().getContext(),
                1,
                new String[] {
                        sourceCode
                },
                null,
                null
        );

        clBuildProgram(program, 0, null, null, null, null);

        kernel = clCreateKernel(program, programName, null);
    }

    public GpuProgram addArgument(cl_mem buffer) {
        clSetKernelArg(kernel, argumentIndex++, Sizeof.cl_mem, Pointer.to(buffer));

        return this;
    }

    public GpuProgram addArgument(int number) {
        clSetKernelArg(kernel, argumentIndex++, Sizeof.cl_uint, Pointer.to(new int[] { number }));

        return this;
    }

    public GpuBufferAllocation allocateBuffer(float[] data, AllocationType allocationType) {
        GpuBufferAllocation bufferAllocation = new GpuBufferAllocation();

        bufferAllocation.pointer = Pointer.to(data);
        bufferAllocation.gpgMemory = clCreateBuffer(
                Gpu.getInstance().getContext(),
                allocationType.getType(),
                Sizeof.cl_float * data.length,
                bufferAllocation.pointer,
                null
        );

        allocations.add(bufferAllocation);

        return bufferAllocation;
    }

    public GpuProgram deallocateBuffer(GpuBufferAllocation bufferAllocation) {
        allocations.remove(bufferAllocation);
        clReleaseMemObject(bufferAllocation.gpgMemory);

        return this;
    }

    public void executeNDRangeKernel(long globalWorkSize, long localSize) {
        clEnqueueNDRangeKernel(
                commandQueue,
                kernel,
                1,
                null,
                new long[] {
                        globalWorkSize
                },
                new long[] {
                        localSize
                },
                0,
                null,
                null
        );
        clFinish(commandQueue);
        argumentIndex = 0;
    }

    public void getFloatBufferResult(GpuBufferAllocation buffer, long bufferSize) {
        clEnqueueReadBuffer(
                commandQueue,
                buffer.gpgMemory,
                CL_TRUE,
                0,
                bufferSize * Sizeof.cl_float,
                buffer.pointer,
                0,
                null,
                null
        );
    }

    private void createCommandQueue() {
        Gpu gpu = Gpu.getInstance();

        commandQueue = clCreateCommandQueueWithProperties(
                gpu.getContext(),
                gpu.getDeviceId(),
                new cl_queue_properties(),
                null
        );
    }

    @Override
    protected void finalize() throws Throwable {

        for (GpuBufferAllocation allocation : allocations) {
            clReleaseMemObject(allocation.gpgMemory);
        }

        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);

        super.finalize();
    }

    public enum AllocationType {
        READ(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR),
        WRITE(CL_MEM_READ_WRITE),
        READ_WRITE(CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR);

        private final long type;
        AllocationType(long type) {
            this.type = type;
        }

        public long getType() {
            return type;
        }
    }
}

package networks.gpu;

import org.jocl.*;

import java.util.LinkedList;
import java.util.List;

import static org.jocl.CL.*;

public class GpuProgram {

    private final cl_program program;
    private final cl_kernel kernel;
    private final List<cl_mem> allocations = new LinkedList<>();
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
        clSetKernelArg(kernel, argumentIndex++, Sizeof.cl_int4, Pointer.to(new int[] { number }));

        return this;
    }

    public cl_mem allocateBuffer(float[] data, AllocationType allocationType) {
        cl_mem bufferAllocation = clCreateBuffer(
                Gpu.getInstance().getContext(),
                allocationType.getType(),
                Sizeof.cl_float * data.length,
                Pointer.to(data),
                null
        );

        allocations.add(bufferAllocation);

        return bufferAllocation;
    }

    public GpuProgram deallocateBuffer(cl_mem buffer) {
        allocations.remove(buffer);
        clReleaseMemObject(buffer);

        return this;
    }

    public void executeNDRangeKernel(long globalWorkSize) {
        clEnqueueNDRangeKernel(
                commandQueue,
                kernel,
                1,
                null,
                new long[] {
                        globalWorkSize
                },
                null,
                0,
                null,
                null
        );

        argumentIndex = 0;
    }

    public void getFloatBufferResult(cl_mem buffer, long bufferSize) {
        clEnqueueReadBuffer(
                commandQueue,
                buffer,
                CL_TRUE,
                0,
                bufferSize * Sizeof.cl_float,
                Pointer.to(buffer),
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

        for (cl_mem allocation : allocations) {
            clReleaseMemObject(allocation);
        }

        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);

        super.finalize();
    }

    public enum AllocationType {
        READ(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR),
        WRITE(CL_MEM_READ_WRITE);

        private final long type;
        AllocationType(long type) {
            this.type = type;
        }

        public long getType() {
            return type;
        }
    }
}

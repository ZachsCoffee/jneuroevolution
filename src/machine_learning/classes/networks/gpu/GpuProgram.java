package networks.gpu;

import org.jocl.*;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import static org.jocl.CL.*;

public class GpuProgram {

    private final cl_program program;
    private final cl_kernel kernel;
    private final List<GpuBufferAllocation> allocations = new LinkedList<>();
    private cl_command_queue commandQueue;
    private int argumentIndex = 0;
    private ByteBuffer byteBuffer;

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

    public GpuProgram addArgumentFloatArray(int size) {
        clSetKernelArg(kernel, argumentIndex++, Sizeof.cl_float * size, null);

        return this;
    }

    public GpuBufferAllocation allocateBuffer(float[] data, AllocationType allocationType) {
        GpuBufferAllocation bufferAllocation = new GpuBufferAllocation();

        bufferAllocation.pointer = Pointer.to(data);
        bufferAllocation.gpuMemory = clCreateBuffer(
                Gpu.getInstance().getContext(),
                allocationType.getType(),
                Sizeof.cl_float * data.length,
                bufferAllocation.pointer,
                null
        );

        allocations.add(bufferAllocation);

        return bufferAllocation;
    }

    public GpuBufferAllocation allocateBufferOverwrite(AllocationType allocationType, GpuBufferAllocation gpuBufferAllocation, long dataLength) {
        GpuBufferAllocation bufferAllocation = new GpuBufferAllocation();
        long mapFlag;

        switch (allocationType) {
            case READ_MAPPED:
            case READ:
                mapFlag = CL_MAP_READ;
                break;
            case WRITE:
                mapFlag = CL_MAP_WRITE;
                break;
            case READ_WRITE:
                mapFlag = CL_MAP_READ | CL_MAP_WRITE;
                break;
            default:
                throw new RuntimeException("Unexpected allocation type "+allocationType);
        }
        bufferAllocation.gpuMemory = gpuBufferAllocation.gpuMemory;
        bufferAllocation.pinnedMemory = clEnqueueMapBuffer(
                commandQueue,
                gpuBufferAllocation.gpuMemory,
                CL_TRUE,
                mapFlag,
                0,
                Sizeof.cl_float * dataLength,
                0,
                null,
                null,
                null
        );

        return bufferAllocation;
    }

    public GpuBufferAllocation allocatePinned(float[] data, AllocationType allocationType) {
        GpuBufferAllocation bufferAllocation = new GpuBufferAllocation();

        long mapFlag;

        switch (allocationType) {
            case READ_MAPPED:
            case READ:
                mapFlag = CL_MAP_READ;
                break;
            case WRITE:
                mapFlag = CL_MAP_WRITE;
                break;
            case READ_WRITE:
                mapFlag = CL_MAP_READ | CL_MAP_WRITE;
                break;
            default:
                throw new RuntimeException("Unexpected allocation type "+allocationType);
        }

        bufferAllocation.gpuMemory = clCreateBuffer(
                Gpu.getInstance().getContext(),
                allocationType.getType(),
                Sizeof.cl_float * data.length,
                null,
                null
        );

        bufferAllocation.pinnedMemory = clEnqueueMapBuffer(
                commandQueue,
                bufferAllocation.gpuMemory,
                CL_TRUE,
                mapFlag,
                0,
                Sizeof.cl_float * data.length,
                0,
                null,
                null,
                null
        );

        allocations.add(bufferAllocation);

        return bufferAllocation;
    }

    public void finish() {
        clFinish(commandQueue);
    }

    public GpuProgram deallocateBuffer(GpuBufferAllocation bufferAllocation) {
        allocations.remove(bufferAllocation);
        clReleaseMemObject(bufferAllocation.gpuMemory);

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

        argumentIndex = 0;
    }

    public void getFloatBufferResult(GpuBufferAllocation buffer, long bufferSize) {
        clEnqueueReadBuffer(
                commandQueue,
                buffer.gpuMemory,
                CL_TRUE,
                0,
                bufferSize * Sizeof.cl_float,
                buffer.pointer,
                0,
                null,
                null
        );
    }

    public void getFloatBufferPinned(GpuBufferAllocation bufferAllocation) {
        clEnqueueUnmapMemObject(
                commandQueue,
                bufferAllocation.gpuMemory,
                bufferAllocation.pinnedMemory,
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
            clReleaseMemObject(allocation.gpuMemory);
        }

        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);

        super.finalize();
    }

    public enum AllocationType {
        READ(CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR),
        READ_MAPPED(CL_MEM_READ_ONLY | CL_MEM_ALLOC_HOST_PTR),
        WRITE(CL_MEM_WRITE_ONLY | CL_MEM_USE_HOST_PTR),
        READ_WRITE(CL_MEM_READ_WRITE | CL_MEM_ALLOC_HOST_PTR);

        private final long type;
        AllocationType(long type) {
            this.type = type;
        }

        public long getType() {
            return type;
        }
    }
}

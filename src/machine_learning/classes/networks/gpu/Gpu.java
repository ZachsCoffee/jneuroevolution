package networks.gpu;

import org.jocl.*;

import static org.jocl.CL.*;

public class Gpu {
    private static final long DEVICE_TYPE = CL_DEVICE_TYPE_GPU;
    private static Gpu gpu = null;

    public static Gpu getInstance() {
        if (gpu == null) {
            gpu = new Gpu();
        }

        return gpu;
    }

    private cl_context context = null;
    private cl_device_id deviceId;
    private cl_platform_id platform;
    private cl_context_properties contextProperties;

    private Gpu() {
        CL.setExceptionsEnabled(true);

        resolvePlatformAndDevice();
        createContextProperties();
        createContext();
    }

    public cl_context getContext() {
        return context;
    }

    public cl_device_id getDeviceId() {
        return deviceId;
    }

    public cl_platform_id getPlatform() {
        return platform;
    }

    public cl_context_properties getContextProperties() {
        return contextProperties;
    }

    private void resolvePlatformAndDevice() {
        int[] platformsArray = new int[1];

        clGetPlatformIDs(0, null, platformsArray);

        cl_platform_id[] platforms = new cl_platform_id[platformsArray[0]];

        clGetPlatformIDs(platforms.length, platforms, null);

        for (int i=0; i<platforms.length; i++) {
            int[] numberOfDevices = new int[1];
            clGetDeviceIDs(platforms[i], DEVICE_TYPE, 0, null, numberOfDevices);

            if (numberOfDevices[0] == 0) {
                continue;
            }

            cl_device_id[] devices = new cl_device_id[numberOfDevices[0]];
            clGetDeviceIDs(platforms[i], DEVICE_TYPE, devices.length, devices, null);

            platform = platforms[i];
            deviceId = devices[0];

            break;
        }

        if (platform == null) throw new RuntimeException(
                "Failed to find available platform!"
        );

        if (deviceId == null) throw new RuntimeException(
                "Failed to find an available GPU!"
        );
    }

    private void createContextProperties() {
        contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
    }

    private void createContext() {
        context = clCreateContext(
                contextProperties,
                1,
                new cl_device_id[] {
                        deviceId
                },
                null,
                null,
                null
        );
    }

    @Override
    protected void finalize() throws Throwable {
        clReleaseContext(context);

        super.finalize();
    }
}

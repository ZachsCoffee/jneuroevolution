package executors;

import maths.matrix.MatrixReader;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConvolutionParallelExecutor extends ConvolutionExecutor {

    private int executedChannels;

    public ConvolutionParallelExecutor(MatrixReader[] channels) {
        super(channels);
    }

    public synchronized void executeParallel(ExecuteStateCallback executeStateCallback) {
        int amountOfThreads = Runtime.getRuntime().availableProcessors();
        if (amountOfThreads != 1) {
            amountOfThreads--;
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(amountOfThreads);

        this.executeStateCallback = Objects.requireNonNull(executeStateCallback);

        executedChannels = 0;
        double[][] output =
        for (int i=0; i<channels.length; i++) {
            int fixedI = i;
            threadPool.submit(() -> {
                output[fixedI] = computeChannel(fixedI);

                executedChannels++;
                if (channels.length == executedChannels) {
                    threadPool.shutdown();
                    executeStateCallback.finish();
                }
                return output[fixedI];
            });
        }
    }

    public interface ExecuteStateCallback {
        void finish(MatrixReader[] output);
    }
}

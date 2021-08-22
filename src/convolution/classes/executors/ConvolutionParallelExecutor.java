package executors;

import maths.matrix.MatrixReader;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ConvolutionParallelExecutor extends ConvolutionExecutor {

    private AtomicInteger executedChannels;

    public ConvolutionParallelExecutor(MatrixReader[] channels) {
        super(channels);
    }

    public synchronized void executeParallel(ExecuteStateCallback executeStateCallback) {
        int amountOfThreads = Runtime.getRuntime().availableProcessors();
        if (amountOfThreads != 1) {
            amountOfThreads--;
        }

        final ExecutorService threadPool = Executors.newFixedThreadPool(amountOfThreads);

        final ExecuteStateCallback checkedCallback = Objects.requireNonNull(executeStateCallback);

        executedChannels = new AtomicInteger();

        for (int i=0; i<channels.length; i++) {
            int fixedI = i;
            threadPool.submit(() -> {
                output[fixedI] = computeChannel(fixedI);

                if (channels.length == executedChannels.incrementAndGet()) {
                    threadPool.shutdown();
                    checkedCallback.finish(output);
                }
            });
        }
    }

    public interface ExecuteStateCallback {
        void finish(MatrixReader[][] output);
    }
}

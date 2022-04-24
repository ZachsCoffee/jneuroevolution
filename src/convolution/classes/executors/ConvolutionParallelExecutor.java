package executors;

import input.ConvolutionInput;
import maths.matrix.MatrixReader;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConvolutionParallelExecutor extends ConvolutionExecutor {

    public static ConvolutionParallelExecutor initialize(ConvolutionInput convolutionInput) {
        return new ConvolutionParallelExecutor(convolutionInput.getChannels());
    }

    public ConvolutionParallelExecutor(MatrixReader[] channels) {
        super(channels);
    }

    public synchronized ExecutionCallback executeParallel() {
        int amountOfThreads = Runtime.getRuntime().availableProcessors();
        if (amountOfThreads != 1) {
            amountOfThreads--;
        }

        if (channels.length < amountOfThreads) {
            amountOfThreads = channels.length;
        }

        final ExecutorService threadPool = Executors.newFixedThreadPool(amountOfThreads);


        List<Callable<MatrixReader[][]>> threadedChannels = new LinkedList<>();

        for (int i=0; i<channels.length; i++) {
            int fixedI = i;
            threadedChannels.add(() -> {
                output[fixedI] = computeChannel(fixedI);
                return output;
            });
        }

        try {
            List<Future<MatrixReader[][]>> promises = threadPool.invokeAll(threadedChannels);
            return new ExecutionCallback(threadPool, promises, output);
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Failed to execute the threads", e);
        }
    }

    public static class ExecutionCallback implements Future<MatrixReader[][]> {
        private final ExecutorService threadPool;
        private final List<Future<MatrixReader[][]>> promises;
        private final MatrixReader[][] output;
        private boolean done = false;

        private ExecutionCallback(
                ExecutorService threadPool,
                List<Future<MatrixReader[][]>> promises,
                MatrixReader[][] output
        ) {
            this.threadPool = threadPool;
            this.promises = promises;
            this.output = output;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public MatrixReader[][] get() {
            if (! done) {
                for (Future<MatrixReader[][]> promise : promises) {
                    try {
                        promise.get();
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException("Interrupted when waiting.", e);
                    }
                    catch (ExecutionException e) {
                        throw new RuntimeException("Error at execution", e);
                    }
                }
                threadPool.shutdown();
                done = true;
            }
            return output;
        }

        @Override
        public MatrixReader[][] get(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException("Not supported");
        }
    }
}

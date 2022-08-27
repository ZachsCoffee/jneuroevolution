package multithreaded;

import dataset.MatrixReaderDataset;

import java.util.concurrent.RecursiveTask;

public class RecursiveEvaluation extends RecursiveTask<Double> {

    private final MatrixReaderDataset dataset;
    private final int threshold;
    private final int startIndex, endIndex;
    private final ErrorFunction errorFunction;

    public RecursiveEvaluation(
        MatrixReaderDataset dataset,
        int threshold,
        ErrorFunction errorFunction
    ) {
        this(dataset, threshold, 0, dataset.getDataLength(), errorFunction);
    }

    private RecursiveEvaluation(
        MatrixReaderDataset dataset,
        int threshold,
        int startIndex,
        int endIndex,
        ErrorFunction errorFunction
    ) {
        this.dataset = dataset;
        this.threshold = threshold;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.errorFunction = errorFunction;
    }

    @Override
    protected Double compute() {
        int partSize = endIndex - startIndex;

        double error = 0;

        if (partSize > threshold) {
            int spitPart = partSize / 2;
            RecursiveEvaluation part1 = new RecursiveEvaluation(
                dataset,
                threshold,
                startIndex,
                Math.min(startIndex + spitPart, dataset.getDataLength()),
                errorFunction
            );

            RecursiveEvaluation part2 = new RecursiveEvaluation(
                dataset,
                threshold,
                startIndex + spitPart,
                endIndex,
                errorFunction
            );

            part1.fork();
            part2.fork();

            error += part1.join() + part2.join();
        }
        else {
            error += errorFunction.computeError(startIndex, endIndex);
        }

        return error;
    }

    public interface ErrorFunction {
        double computeError(int startIndex, int endIndex);
    }
}

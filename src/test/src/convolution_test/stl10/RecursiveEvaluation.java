package convolution_test.stl10;

import data_manipulation.Dataset;
import maths.ArrayUtils;
import networks.interfaces.Network;

import java.util.Objects;
import java.util.concurrent.RecursiveTask;

public class RecursiveEvaluation extends RecursiveTask<Double> {

    private static final int THRESHOLD = 1000;
    private final int startIndex, endIndex;
    private final Dataset dataset;
    private final Network network;
    private final double[][] predictionValues;

    public RecursiveEvaluation(
        int startIndex,
        int endIndex,
        Dataset dataset,
        Network network,
        double[][] predictionValues
    ) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.dataset = Objects.requireNonNull(dataset);
        this.network = Objects.requireNonNull(network);
        this.predictionValues = predictionValues;
    }

    @Override
    protected Double compute() {
        double error = 0;

        int partSize = endIndex - startIndex;
        if (partSize > THRESHOLD) {
            int splitSize = partSize / 2;

            RecursiveEvaluation part1 = new RecursiveEvaluation(
                startIndex,
                startIndex + splitSize,
                dataset,
                network,
                predictionValues
            );
            RecursiveEvaluation part2 = new RecursiveEvaluation(
                startIndex + splitSize,
                endIndex,
                dataset,
                network,
                predictionValues
            );

            part1.fork();
            part2.fork();

            error += part1.join() + part2.join();
        }
        else {
            for (int i = startIndex; i < endIndex && i < dataset.SIZE; i++) {
                double[] result = network.compute(dataset.features[i]);
//                double prediction = (int) Math.round(result[0] * 9);
//                double prediction = Math.round(result[(int)dataset.targets[i][0]]);
                int predictionPosition = ArrayUtils.maxPosition(result);
                if (dataset.targets[i][0] != predictionPosition) {
//                        error += result[predictionPosition] - result[(int)dataset.targets[i][0]];
                    error++;
                }
//                    double prediction = result[(int) dataset.targets[i][0]];
//                    error += Math.log(prediction == 0
//                                          ? .1
//                                          : prediction);

                if (predictionValues != null) {
                    predictionValues[i][0] = predictionPosition;
                    predictionValues[i][1] = dataset.targets[i][0];
                }
            }
        }

        return error;
    }
}

package convolution_test.stl10;

import basic_gui.DataBinder;
import basic_gui.ExecutionResponse;
import basic_gui.Gui;
import basic_gui.ProblemExecutor;
import data_manipulation.Dataset;
import data_manipulation.DatasetSpecs;
import data_manipulation.DatasetSplitter;
import data_manipulation.ProblemDatasets;
import evolution_builder.components.Mutation;
import evolution_builder.components.PercentOfFitness;
import evolution_builder.components.Recombination;
import evolution_builder.components.Selection;
import evolution_builder.population.Population;
import execution.EvaluationTarget;
import files.binary.BinaryDatasetUtils;
import functions.ActivationFunction;
import maths.ArrayUtils;
import maths.Function;
import maths.MinMax;
import networks.interfaces.Network;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;
import neuroevolution.Problem;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.ForkJoinTask.invokeAll;

public class Stl10Problem extends ProblemExecutor {
    private final int EVALUATION_THREADS = 5;
    private final ExecutorService executorService = Executors.newFixedThreadPool(EVALUATION_THREADS * 2);

    public static void main(String[] args) {
        Gui.create(Stl10Problem.class);
    }

    public Stl10Problem(DataBinder dataBinder) {
        super(dataBinder);

        Path mainPath = Paths.get("/home/zachs/Develop/MachineLearning/stl10_binary");

        double[][] trainingData = BinaryDatasetUtils.loadFrom(mainPath.resolve("images/convoluted/train.cov").toFile());
        double[][] testingData = BinaryDatasetUtils.loadFrom(mainPath.resolve("images/convoluted/test.cov").toFile());

        DatasetSpecs trainingDataset = DatasetSpecs.init(trainingData)
            .setTrainingSize(1)
//            .setValidationSize(.2)
            .setTargetsCount(1)
            .setup();

        DatasetSpecs testingDataset = DatasetSpecs.init(testingData)
            .setTestingSize(1)
            .setTargetsCount(1)
            .setup();

        POPULATION_SIZE = 20;
        THREADS = 2;
        EPOCHS = 10;
        MIGRATION_PERCENT = .1;

        EVALUATION_TARGET = EvaluationTarget.EVOLUTION_BEST;

        ProblemDatasets trainingSplit = DatasetSplitter.split(trainingDataset);

        this.trainingDataset = trainingSplit.getTrainingDataset();
//        this.validationDataset = trainingSplit.getValidationDataset();
        this.testingDataset = DatasetSplitter.split(testingDataset).getTestingDataset();


        setDynamicMutation(new MinMax(600, 2000), EPOCHS);
    }

    @Override
    public Problem getProblem() {
        return this;
    }

    @Override
    public void computePercentOfFitness(Population population) {
        PercentOfFitness.percentFromCurrentBestRanked(population);
    }

    @Override
    public Population recombinationOperator(Population population, int epoch) {
        return Recombination.random(population, 10, PERSON);
    }

    @Override
    public Population selectionMethod(Population population) {
        return Selection.tournament(population, 7, PERCENT_OF_FITNESS);
    }

    @Override
    public void mutationMethod(Population population, int epoch, int maxEpoch) {
        Mutation.mutation(population, getMutationChange(epoch), 1, true, PERSON);
    }

    @Override
    public Network buildNetwork(int maxStartValue) {
        hiddenLayerFunction = ActivationFunction.GAUSS.getFunction();
        Function middleFunction = ActivationFunction.GAUSS.getFunction();
        outputLayerFunction = ActivationFunction.SIGMOID.getFunction();

        return NeuralNetworkBuilder.initialize(trainingDataset.features[0].length, 500, hiddenLayerFunction)
            .addLayer(500, hiddenLayerFunction)
            .addLayer(1, outputLayerFunction)
            .build();
    }

    @Override
    public Network buildRandomNetwork(int maxStartValue) {
        return buildNetwork(maxStartValue);
    }

    @Override
    public double evaluateNetwork(Network network, Dataset dataset) {
        return dataset.SIZE - datasetEvaluation(network, dataset, null);
    }

    @Override
    public EvaluationResult evaluation(Network network, Dataset dataset) {
        double[][] predictionValues = new double[dataset.SIZE][2];

        double error = datasetEvaluation(network, dataset, predictionValues);
        System.out.println("Accuracy: " + (100 * (dataset.SIZE - error) / dataset.SIZE));
        return new EvaluationResult(predictionValues, error);
    }

    @Override
    public void executionEnds(ExecutionResponse[] responses) {

    }

//    private double datasetEvaluation(Network network, Dataset dataset, final double[][] predictionValues) {
//        double errorCount = 0;
//
//        double error = 0;
//
//        for (int i = 0; i < dataset.SIZE; i++) {
//            double[] result = network.compute(dataset.features[i]);
//            int predictionPosition = ArrayUtils.maxPosition(result);
//            double prediction = result[(int) dataset.targets[i][0]];
//            error += Math.log(prediction == 0
//                                  ? .1
//                                  : prediction);
//
//            if (predictionValues != null) {
//                predictionValues[i][0] = predictionPosition;
//                predictionValues[i][1] = dataset.targets[i][0];
//            }
//        }
//
//        return - error;
//    }

    private double datasetEvaluation(Network network, Dataset dataset, final double[][] predictionValues) {
        int partSize = (int) Math.ceil((double)dataset.SIZE / EVALUATION_THREADS);
        List<Callable<Double>> tasks = new LinkedList<>();

        for (int j=0; j<EVALUATION_THREADS; j++) {
            int threadIndex = j;
            tasks.add(() -> {
                double error = 0;

                int partStart = threadIndex * partSize;
                int partEnd = partStart + partSize;

                for (int i = partStart; i < partEnd && i < dataset.SIZE; i++) {
                    double[] result = network.compute(dataset.features[i]);
                    double prediction = (int) Math.round(result[0] * 9);
//                    int predictionPosition = ArrayUtils.maxPosition(result);
                    if ((int)dataset.targets[i][0] != prediction) {
//                        error += result[predictionPosition] - result[(int)dataset.targets[i][0]];
                        error++;
                    }
//                    double prediction = result[(int) dataset.targets[i][0]];
//                    error += Math.log(prediction == 0
//                                          ? .1
//                                          : prediction);

                    if (predictionValues != null) {
                        predictionValues[i][0] = prediction;
                        predictionValues[i][1] = dataset.targets[i][0];
                    }
                }

                return error;
            });
        }

        try {
            List<Future<Double>> results = executorService.invokeAll(tasks);
            double error = 0;
            for (Future<Double> result : results) {
                try {
                    error += result.get();
                }
                catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            return error;
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

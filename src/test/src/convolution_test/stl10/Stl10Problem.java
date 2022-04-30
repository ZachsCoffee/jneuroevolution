package convolution_test.stl10;

import basic_gui.DataBinder;
import basic_gui.ExecutionResponse;
import basic_gui.Gui;
import basic_gui.ProblemExecutor;
import data_manipulation.Dataset;
import data_manipulation.DatasetSpecs;
import data_manipulation.DatasetSplitter;
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

public class Stl10Problem extends ProblemExecutor {

    public Stl10Problem(DataBinder dataBinder) {
        super(dataBinder);

        Path mainPath = Paths.get("/home/zachs/Develop/MachineLearning/stl10_binary");

        double[][] trainingData = BinaryDatasetUtils.loadFrom(mainPath.resolve("images/convoluted/train.cov").toFile());
        double[][] testingData = BinaryDatasetUtils.loadFrom(mainPath.resolve("images/convoluted/test.cov").toFile());

        DatasetSpecs trainingDataset = DatasetSpecs.init(trainingData)
            .setTrainingSize(1)
            .setTargetsCount(1)
            .setup();

        DatasetSpecs testingDataset = DatasetSpecs.init(testingData)
            .setTestingSize(1)
            .setTargetsCount(1)
            .setup();

        POPULATION_SIZE = 20;
        THREADS = 5;
        EPOCHS = 100;
        MIGRATION_PERCENT = .2;
        EVALUATION_TARGET = EvaluationTarget.EVOLUTION_BEST;

        this.trainingDataset = DatasetSplitter.split(trainingDataset).getTrainingDataset();
        this.testingDataset = DatasetSplitter.split(testingDataset).getTestingDataset();

        setDynamicMutation(new MinMax(600, 2000), EPOCHS);
    }

    @Override
    public void computePercentOfFitness(Population population) {
        PercentOfFitness.percentFromCurrentBestRanked(population);
    }

    @Override
    public Population recombinationOperator(Population population, int epoch) {
        return Recombination.random(population, 2, PERSON);
    }

    @Override
    public Population selectionMethod(Population population) {
        return Selection.tournament(population, 5, PERCENT_OF_FITNESS);
    }

    @Override
    public void mutationMethod(Population population, int epoch, int maxEpoch) {
        Mutation.mutation(population, getMutationChange(epoch), 10, true, PERSON);
    }

    @Override
    public Network buildNetwork(int maxStartValue) {
        hiddenLayerFunction = ActivationFunction.TANH.getFunction();
        Function middleFunction = ActivationFunction.GAUSS.getFunction();
        outputLayerFunction = ActivationFunction.SIGMOID.getFunction();

        return NeuralNetworkBuilder.initialize(trainingDataset.features[0].length, 10, middleFunction)
            .addLayer(10, middleFunction)
            .addLayer(10, outputLayerFunction)
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
    public Problem getProblem() {
        return this;
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

    private double datasetEvaluation(Network network, Dataset dataset, final double[][] predictionValues) {
        double errorCount = 0;

        for (int i=0; i<dataset.SIZE; i++) {
            double[] result = network.compute(dataset.features[i]);
            int predictionPosition = ArrayUtils.maxPosition(result);

            if (dataset.targets[i][0] != predictionPosition) {
                errorCount++;
            }

            if (predictionValues != null) {
                predictionValues[i][0] = predictionPosition;
                predictionValues[i][1] = dataset.targets[i][0];
            }
        }

        return errorCount;
    }

    public static void main(String[] args) {
        Gui.create(Stl10Problem.class);
    }
}

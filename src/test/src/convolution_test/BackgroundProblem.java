package convolution_test;

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
import maths.MinMax;
import networks.interfaces.Network;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;
import networks.multilayer_perceptron.network.NeuralNetwork;
import networks.multilayer_perceptron.serializers.NetworkJsonSerializer;
import neuroevolution.Problem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BackgroundProblem extends ProblemExecutor {

    public BackgroundProblem(DataBinder dataBinder) {
        super(dataBinder);
        long start = System.currentTimeMillis();
//        double[][] allData = CSVFileReader.readNumbersFile(
//                new File("/home/zachs/Develop/Java/artificialintelligence/datasets/dataset1.csv"), ","
//        );

        double[][] allData = BinaryDatasetUtils.loadFrom(
                new File("/home/zachs/Develop/Java/artificialintelligence/datasets/dataset3.b")
        );

        double someDataSize = 100_000d;
        int period = (int) Math.ceil(allData.length / someDataSize);
        double[][] someData = new double[(int)Math.ceil(allData.length / (double)period)][];
//
        int p=0;
        for (int i=0; i<allData.length; i++) {
            if (i % period == 0) {
                someData[p++] = allData[(int) (Math.random() * allData.length)];
            }
        }

        DatasetSpecs datasetSpecs = DatasetSpecs.init(someData, .6, .2)
                .setTargetsCount(36)
                .setValidationSize(.2)
                .setup();

        ProblemDatasets datasets = DatasetSplitter.split(datasetSpecs);

        trainingDataset = datasets.getTrainingDataset();
        testingDataset = datasets.getTestingDataset();
        validationDataset = datasets.getValidationDataset();

        System.out.println(
                "Load data time: "+ (System.currentTimeMillis() - start)+"\n"+
                "Total features: "+allData.length+" selected: "+someData.length+"\n"+
                "Feature length: "+trainingDataset.features[0].length+" targets length: "+trainingDataset.targets[0].length+"\n"+
                "Training features: "+trainingDataset.SIZE+"\n"+
                "Test features: "+testingDataset.SIZE+"\n"+
                "Validation features: "+validationDataset.SIZE
        );

        POPULATION_SIZE = 20;
        THREADS = 10;
        EPOCHS = 100;
        MIGRATION_PERCENT = .1;

//        PERCENT_OF_FITNESS = true;

        EVALUATION_TARGET = EvaluationTarget.VALIDATION_BEST;
        setDynamicMutation(new MinMax(600, 2000), EPOCHS);
    }

    @Override
    public EvaluationResult evaluation(Network network, Dataset dataset) {

        double[][] predictionValues = new double[dataset.SIZE][2];

        double error = datasetEvaluation(network, dataset, predictionValues);

        return new EvaluationResult(predictionValues, error);
    }

    @Override
    public void executionEnds(ExecutionResponse[] responses) {
        ExecutionResponse executionResponse = findBest(responses);

        try {
            Files.write(
                    Paths.get("/home/zachs/Develop/Java/artificialintelligence/networks/network1.json"),
                    NetworkJsonSerializer.toJson((NeuralNetwork)executionResponse.getNetwork()).getBytes(StandardCharsets.UTF_8)
            );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
        return Selection.tournament(population, 3, PERCENT_OF_FITNESS);
    }

    @Override
    public void mutationMethod(Population population, int epoch, int maxEpoch) {
        Mutation.mutation(population, getMutationChange(epoch), 1, true, PERSON);
    }

    @Override
    public Network buildNetwork(int maxStartValue) {
        hiddenLayerFunction = ActivationFunction.GAUSS.getFunction();
//        Function middleFunction = ActivationFunctions.gauss();
        outputLayerFunction = ActivationFunction.SIGMOID.getFunction();

        return NeuralNetworkBuilder.initialize(trainingDataset.features[0].length, 9, hiddenLayerFunction)
                .addLayer(5, hiddenLayerFunction)
                .addLayer(36, outputLayerFunction)
                .build();
    }

    @Override
    public Network buildRandomNetwork(int maxStartValue) {
        return buildNetwork(maxStartValue);
    }

    @Override
    public double evaluateNetwork(Network network, Dataset dataset) {
        double evaluation = datasetEvaluation(network, dataset, null);
        if (dataset.SIZE < evaluation) {
            throw new RuntimeException("kkk");
        }
        return dataset.SIZE - evaluation;
    }

    private double datasetEvaluation(Network network, Dataset dataset, final double[][] predictionValues) {
        double errorSum = 0;
        int p = 0;

        for (int i=0; i<dataset.SIZE; i++) {
            double[] result = network.compute(dataset.features[i]);
            int errorCount = 0;

            for (int j=0; j < result.length; j++) {
                if ((int) Math.round(result[j]) != dataset.targets[i][j]) {
                    errorCount++;
                }
            }
            double prediction = (double) errorCount / dataset.targets[i].length;
            if (predictionValues != null && i < predictionValues.length) {
                predictionValues[i][0] = prediction;
                predictionValues[i][1] = prediction != 0 ? 1 : 0;
            }

            errorSum += prediction;
        }

        return errorSum;
    }

//    private double datasetEvaluation(Network network, Dataset dataset, final double[][] predictionValues) {
//        int errorCount = 0;
//        int period = (int) Math.ceil(dataset.SIZE / 100d);
//        int p = 0;
//        for (int i=0; i<dataset.SIZE; i++) {
//            int result = (int) Math.round(network.compute(dataset.features[i])[0]);
//            if (predictionValues != null && i % period == 0) {
//                predictionValues[p++][0] = result;
//            }
//
//            if (result != (int)dataset.targets[i][0]) {
//                errorCount++;
//            }
//        }
//
//        return 100d * errorCount / dataset.SIZE;
//    }

    @Override
    public Problem getProblem() {
        return this;
    }

    public static void main(String[] args) {
        Gui.create(BackgroundProblem.class);
    }
}

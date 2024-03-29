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
import java.util.LinkedList;
import java.util.List;

public class BackgroundProblem extends ProblemExecutor {

    public BackgroundProblem(DataBinder dataBinder) {
        super(dataBinder);
        long start = System.currentTimeMillis();
//        double[][] allData = CSVFileReader.readNumbersFile(
//                new File("/home/zachs/Develop/Java/artificialintelligence/datasets/dataset1.csv"), ","
//        );

        double[][] allData = BinaryDatasetUtils.loadFrom(
                new File("/home/zachs/Develop/Java/artificialintelligence/datasets/dataset_border.b")
        );

        double someDataSize = 100_000d;
        int period = (int) Math.ceil(allData.length / someDataSize);
        List<double[]> someData = new LinkedList<>();
//        double[][] someData = new double[(int)Math.ceil(allData.length / (double)period)][];
//
        int good = 0, bad = 0;
        int p=0;
        for (int i=0; i<allData.length; i++) {
//            if (i % period == 0) {
//                someData[p++] = allData[(int) (Math.random() * allData.length)];
//            }
//            someData.add(allData[i]);
//
            if (allData[i][allData[i].length -1] == 1) {
                if (good <= bad) {
                    someData.add(allData[i]);
                    good++;
                }
            }
            else {
                if (bad <= good) {
                    someData.add(allData[i]);
                    bad++;
                }
            }
        }

        DatasetSpecs datasetSpecs = DatasetSpecs.init(someData.toArray(new double[0][]), .6, .2)
                .setTargetsCount(1)
                .setValidationSize(.2)
                .setup();

        ProblemDatasets datasets = DatasetSplitter.split(datasetSpecs);

        trainingDataset = datasets.getTrainingDataset();
        testingDataset = datasets.getTestingDataset();
        validationDataset = datasets.getValidationDataset();

        System.out.println(
                "Load data time: "+ (System.currentTimeMillis() - start)+"\n"+
                "Total features: "+allData.length+" selected: "+someData.size()+"\n"+
                "Feature length: "+trainingDataset.features[0].length+" targets length: "+trainingDataset.targets[0].length+"\n"+
                "Training features: "+trainingDataset.SIZE+"\n"+
                "Test features: "+testingDataset.SIZE+"\n"+
                "Validation features: "+validationDataset.SIZE
        );

        POPULATION_SIZE = 30;
        THREADS = 10;
        EPOCHS = 500;
        MIGRATION_PERCENT = .1;

//        PERCENT_OF_FITNESS = true;

        EVALUATION_TARGET = EvaluationTarget.VALIDATION_BEST;
        setDynamicMutation(new MinMax(600, 2000), EPOCHS);
    }

    @Override
    public EvaluationResult evaluation(Network network, Dataset dataset) {

        double[][] predictionValues = new double[dataset.SIZE][2];

        double error = datasetEvaluation(network, dataset, predictionValues);
        System.out.println("Error percent: "+(100d * error / dataset.SIZE));
        return new EvaluationResult(predictionValues, error);
    }

    @Override
    public void executionEnds(ExecutionResponse[] responses) {
        ExecutionResponse executionResponse = findBest(responses);

        try {
            Files.write(
                    Paths.get("/home/zachs/Develop/Java/artificialintelligence/networks/network3.json"),
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
        return Selection.tournament(population, 5, PERCENT_OF_FITNESS);
    }

    @Override
    public void mutationMethod(Population population, int epoch, int maxEpoch) {
        Mutation.mutation(population, getMutationChange(epoch), 1, true, PERSON);
    }

    @Override
    public Network buildNetwork(int maxStartValue) {
        hiddenLayerFunction = ActivationFunction.TANH.getFunction();
//        Function middleFunction = ActivationFunctions.gauss();
        outputLayerFunction = ActivationFunction.SIGMOID.getFunction();

        return NeuralNetworkBuilder.initialize(trainingDataset.features[0].length, 20, hiddenLayerFunction)
                .addLayer(20, hiddenLayerFunction)
                .addLayer(1, outputLayerFunction)
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
        double errorCount = 0;

        for (int i=0; i<dataset.SIZE; i++) {
            double[] result = network.compute(dataset.features[i]);

            if ((int) Math.round(result[0]) != dataset.targets[i][0]) {
                errorCount++;
            }

            if (predictionValues != null) {
                predictionValues[i][0] = Math.round(result[0]);
                predictionValues[i][1] = dataset.targets[i][0];
            }
        }

        return errorCount;
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

package convolution_test.stl10;

import common.TrainableConvolutionSystemBuilder;
import core.layer.MatrixReader;
import core.layer.TrainableLayer;
import dataset.MatrixReaderDataset;
import evolution.AbstractConvolution2DProblem;
import evolution.ConvolutionGenes;
import evolution.ConvolutionPersonManager;
import evolution_builder.components.Mutation;
import evolution_builder.components.Recombination;
import evolution_builder.components.Selection;
import evolution_builder.population.Population;
import execution.NeuroevolutionPersonManager;
import functions.ActivationFunction;
import input.RawImageInput;
import layers.pool.PoolFunction;
import maths.MinMax;
import multithreaded.RecursiveEvaluation;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class Stl10ConvolutionProblem extends AbstractConvolution2DProblem {

    public static final int EPOCHS = 100;
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool(7);
    private final ConvolutionPersonManager personManager;
    private final ConvolutionGenes convolutionGenes;
    private final double[][] evaluationResult = new double[2][10];

    protected Stl10ConvolutionProblem() {
        personManager = new ConvolutionPersonManager(this);
        convolutionGenes = new ConvolutionGenes();

        setDynamicMutation(new MinMax(1000, 2000), EPOCHS);

        Path basePath = Paths.get("/home/zachs/Develop/MachineLearning/stl10_binary");

        List<Integer> specificLabels = new ArrayList<>() {{
            add(1);
            add(2);
        }};

        int trainLimit = 200;
        int testLimit = 200;

        List<MatrixReader[]> trainImages = readX(basePath.resolve("images/train"), trainLimit);

        List<double[]> trainTargets = readY(basePath.resolve("train_y.bin"), trainLimit);

        trainTargets.stream()
            .collect(Collectors.groupingBy(doubles -> doubles[0], LinkedHashMap::new, Collectors.counting()))
            .entrySet()
            .stream()
            .sorted((o1, o2) -> (int) (o2.getValue() - o1.getValue()))
            .forEach(doubleLongEntry -> System.out.println("Class: " + doubleLongEntry.getKey() +" count: " + doubleLongEntry.getValue()));

        validationDataset = toDataset(
            trainImages.subList(trainLimit / 2, trainLimit),
            trainTargets.subList(trainLimit / 2, trainLimit)
        );

//        pickSpecificLabels(trainImages, trainTargets, specificLabels);

        System.out.println("Train size:" + trainTargets.size());

        trainingDataset = toDataset(
            trainImages.subList(0, trainLimit / 2),
            trainTargets.subList(0, trainLimit / 2)
        );

        List<MatrixReader[]> testImages = readX(basePath.resolve("images/test"), testLimit);

        List<double[]> testTargets = readY(basePath.resolve("test_y.bin"), testLimit);

//        pickSpecificLabels(testImages, testTargets, specificLabels);

        System.out.println("Test size:" + testTargets.size());

        testingDataset = toDataset(testImages, testTargets);
    }

    private MatrixReaderDataset toDataset(
        List<MatrixReader[]> trainImages,
        List<double[]> trainTargets
    ) {
        return new MatrixReaderDataset(
            trainImages.toArray(new MatrixReader[0][]),
            trainTargets.toArray(new double[0][])
        );
    }

    @Override
    public NeuroevolutionPersonManager<TrainableLayer> getPersonManager() {
        return personManager;
    }

    @Override
    public TrainableLayer buildConvolution() {
        return TrainableConvolutionSystemBuilder.getInstance(3, 96, 96)
            .addConvolutionLayer()
            .setKernelsPerChannel(10)
            .setStride(3)
            .setSumKernels(true)
            .and()
            .addConvolutionLayer()
            .setStride(1)
            .setKernelsPerChannel(5)
            .setSumKernels(true)
            .and()
            .addPoolingLayer(PoolFunction.MAX, 3, 1)
            .addNeuralNetworkLayer()
            .addLayer(9, ActivationFunction.GAUSS.getFunction())
            .addLayer(5, ActivationFunction.GAUSS.getFunction())
            .addLayer(5, ActivationFunction.GAUSS.getFunction())
            .addLayer(10, ActivationFunction.SIGMOID.getFunction())
            .and()
            .build();
    }

    @Override
    public double evaluateFitness(TrainableLayer convolution, MatrixReaderDataset dataset) {
        RecursiveEvaluation task = new RecursiveEvaluation(
            dataset,
            50,
            (startIndex, endIndex) -> {
                double error = 0;

                for (int i = startIndex; i < endIndex; i++) {
                    double[][] results = evaluateSystemAtIndex(convolution, dataset, i);

                    int targetIndex = (int) results[1][0];
                    int prediction = (int) Math.round(results[0][targetIndex]);
                    if (targetIndex != prediction) {
                        error++;
                    }
                }

                return error;
            }
        );

        return dataset.getDataLength() - forkJoinPool.invoke(task);
    }

    private void softMax(double[] predictions) {
        double sum = 0;

        for (int i = 0; i < predictions.length; i++) {
            sum += Math.exp(predictions[i]);
        }
        for (int i = 0; i < predictions.length; i++) {
            predictions[i] = Math.exp(predictions[i]) / sum;
        }
    }

    @Override
    public void computePercentOfFitness(Population<TrainableLayer> population) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Population<TrainableLayer> recombinationOperator(
        Population<TrainableLayer> population,
        int epoch
    ) {
        return Recombination.random(population, 5, convolutionGenes);
    }

    @Override
    public Population<TrainableLayer> selectionMethod(Population<TrainableLayer> population) {
        return Selection.tournament(population, 3, false);
    }

    @Override
    public void mutationMethod(Population<TrainableLayer> population, int epoch, int maxEpoch) {
        Mutation.mutation(population, getMutationChange(epoch), 1, true, convolutionGenes);
    }

    @Override
    public double[][] evaluateSystemAtIndex(TrainableLayer convolution, MatrixReaderDataset dataset, int index) {
        double[] results = convolution.execute(dataset.getData()[index])[0].getRow(0);
        evaluationResult[0] = results;
        evaluationResult[1][0] = (int) dataset.getTargets()[index][0] - 1;
        return evaluationResult;
    }

    private void pickSpecificLabels(List<MatrixReader[]> images, List<double[]> targets, List<Integer> specificLabels) {
        int i = 0;
        while (i < images.size()) {
            if (! specificLabels.contains((int) targets.get(i)[0])) {
                images.remove(i);
                targets.remove(i);
            }
            else {
                i++;
            }
        }
    }

    private List<MatrixReader[]> readX(Path folder, int limit) {
        File[] files = folder.toFile().listFiles();
        List<MatrixReader[]> data = new ArrayList<>();

        try {
            int count = 0;
            for (File file : files) {
                data.add(new RawImageInput(ImageIO.read(file)).getChannels());
                if (++ count == limit) {
                    break;
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read data from the image.", e);
        }

        return data;
    }

    private List<double[]> readY(Path file, int limit) {
        List<double[]> targets = new ArrayList<>();

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file.toString()))) {
            int label;
            int count = 0;
            while ((label = inputStream.read()) != - 1) {
                targets.add(new double[]{label});
                if (++ count == limit) {
                    break;
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read the targets.", e);
        }

        return targets;
    }

    private int bestIndex(double[] results) {
        double maxValue = results[0];
        int maxValueIndex = 0;
        for (int i = 1; i < results.length; i++) {
            if (results[i] > maxValue) {
                maxValue = results[i];
                maxValueIndex = i;
            }
        }

        return maxValueIndex;
    }
}

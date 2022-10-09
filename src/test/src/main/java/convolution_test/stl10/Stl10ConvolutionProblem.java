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
import execution.common.NeuroevolutionPersonManager;
import executors.TrainableSystem;
import functions.ActivationFunction;
import input.RawImageInput;
import maths.MinMax;
import multithreaded.RecursiveEvaluation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class Stl10ConvolutionProblem extends AbstractConvolution2DProblem {

    public static final int EPOCHS = 200;
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool(9);
    private final ConvolutionPersonManager personManager;
    private final ConvolutionGenes convolutionGenes;
    private final double[][] evaluationResult = new double[2][10];

    protected Stl10ConvolutionProblem() {
        personManager = new ConvolutionPersonManager(this);
        convolutionGenes = new ConvolutionGenes();

        setDynamicMutation(new MinMax(10000, 10000), EPOCHS);

        cifar();
//        stl();
    }

    private void cifar() {
        Path basePath = Paths.get("/home/zachs/Develop/Python/quick_plot/cifar-10");

        List<Integer> specificLabels = new ArrayList<>() {{
            add(1);
            add(2);
        }};

        int trainLimit = 600;
        int testLimit = 200;

        List<MatrixReader[]> trainImages = readX(basePath.resolve("output/training/images"), trainLimit);

        List<double[]> trainTargets = readY(basePath.resolve("output/training/targets.b"), trainLimit);

        trainTargets.stream()
            .collect(Collectors.groupingBy(doubles -> doubles[0], LinkedHashMap::new, Collectors.counting()))
            .entrySet()
            .stream()
            .sorted((o1, o2) -> (int) (o2.getValue() - o1.getValue()))
            .forEach(doubleLongEntry -> System.out.println("Class: " + doubleLongEntry.getKey() + " count: " + doubleLongEntry.getValue()));

        trainingDataset = toDataset(
            trainImages.subList(0, trainLimit / 2),
            trainTargets.subList(0, trainLimit / 2)
        );

//        toImage(trainingDataset, "t");

        validationDataset = toDataset(
            trainImages.subList(trainLimit / 2, trainLimit),
            trainTargets.subList(trainLimit / 2, trainLimit)
        );

//        toImage(validationDataset, "v");

//        pickSpecificLabels(trainImages, trainTargets, specificLabels);

        System.out.println("Train size:" + trainTargets.size());

        List<MatrixReader[]> testImages = readX(basePath.resolve("output/testing/images"), testLimit);

        List<double[]> testTargets = readY(basePath.resolve("output/testing/targets.b"), testLimit);

//        pickSpecificLabels(testImages, testTargets, specificLabels);

        System.out.println("Test size:" + testTargets.size());

        testingDataset = toDataset(testImages, testTargets);
//        toImage(testingDataset, "T");

        ((TrainableSystem) buildConvolution()).printSchema(testImages.get(0));
    }

    private void stl() {
        Path basePath = Paths.get("/home/zachs/Develop/MachineLearning/stl10_binary");

        List<Integer> specificLabels = new ArrayList<>() {{
            add(1);
            add(2);
        }};

        int trainLimit = 12000;
        int testLimit = 200;

        List<MatrixReader[]> trainImages = readX(basePath.resolve("images/train"), trainLimit);

        List<double[]> trainTargets = readY(basePath.resolve("train_y.bin"), trainLimit);

        trainTargets.stream()
            .collect(Collectors.groupingBy(doubles -> doubles[0], LinkedHashMap::new, Collectors.counting()))
            .entrySet()
            .stream()
            .sorted((o1, o2) -> (int) (o2.getValue() - o1.getValue()))
            .forEach(doubleLongEntry -> System.out.println("Class: " + doubleLongEntry.getKey() + " count: " + doubleLongEntry.getValue()));

        trainingDataset = toDataset(
            trainImages.subList(0, trainLimit / 2),
            trainTargets.subList(0, trainLimit / 2)
        );

//        toImage(trainingDataset, "t");

        validationDataset = toDataset(
            trainImages.subList(trainLimit / 2, trainLimit),
            trainTargets.subList(trainLimit / 2, trainLimit)
        );

//        toImage(validationDataset, "v");

//        pickSpecificLabels(trainImages, trainTargets, specificLabels);

        System.out.println("Train size:" + trainTargets.size());

        List<MatrixReader[]> testImages = readX(basePath.resolve("images/test"), testLimit);

        List<double[]> testTargets = readY(basePath.resolve("test_y.bin"), testLimit);

//        pickSpecificLabels(testImages, testTargets, specificLabels);

        System.out.println("Test size:" + testTargets.size());

        testingDataset = toDataset(testImages, testTargets);
//        toImage(testingDataset, "T");

        ((TrainableSystem) buildConvolution()).printSchema(testImages.get(0));
    }

    private void toImage(MatrixReaderDataset dataset, String prefix) {

        for (int i=0; i<10; i++) {
            BufferedImage image = new BufferedImage(96, 96, BufferedImage.TYPE_INT_RGB);

            for (int j=0; j<dataset.getData()[i].length; j++) {

                for (int r=0; r<dataset.getData()[i][j].getRowsCount(); r++) {
                    for (int c=0; c<dataset.getData()[i][j].getColumnsCount(); c++) {
                        image.setRGB(c, r, image.getRGB(c, r) | ((int)dataset.getData()[i][j].valueAt(r, c) << 8 * (2 - j)));
                    }
                }
            }

            try {
                ImageIO.write(image, "jpg", new File("./"+prefix+i));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
        return TrainableConvolutionSystemBuilder.getInstance(3, 32, 32)
            .addConvolutionLayer()
            .setKernelsPerChannel(4)
            .setSumKernels(true)
            .setStride(2)
            .and()
            .addConvolutionLayer()
            .setKernelsPerChannel(5)
            .setSumKernels(true)
            .setKernelsPerChannel(3)
            .and()
            .addPoolingLayer()
            .and()
            .addConvolutionLayer()
            .setKernelsPerChannel(1)
            .and()
            .addPoolingLayer()
            .setStride(2)
            .and()
            .addNeuralNetworkLayer()
            .addLayer(200, ActivationFunction.GROUND_RELU.getFunction())
            .addLayer(100, ActivationFunction.GROUND_RELU.getFunction())
            .addLayer(70, ActivationFunction.GROUND_RELU.getFunction())
            .addLayer(10, ActivationFunction.SIGMOID.getFunction())
            .and()
            .build();
    }

//    @Override
//    public double evaluateFitness(TrainableLayer convolution, MatrixReaderDataset dataset) {
//        RecursiveEvaluation task = new RecursiveEvaluation(
//            dataset,
//            100,
//            (startIndex, endIndex) -> {
//                double error = 0;
//
//                for (int i = startIndex; i < endIndex; i++) {
//                    double[][] results = evaluateSystemAtIndex(convolution, dataset, i);
//
//                    int targetIndex = (int) results[1][0];
//                    int predictedIndex = bestIndex(results[0]);
//                    if (targetIndex != predictedIndex) {
//                        error++;
//                    }
//                }
//
//                return error;
//            }
//        );
//
//        return dataset.getDataLength() - forkJoinPool.invoke(task);
//    }

//    @Override
//    public double evaluateFitness(TrainableLayer convolution, MatrixReaderDataset dataset) {
//        RecursiveEvaluation task = new RecursiveEvaluation(
//            dataset,
//            50,
//            (startIndex, endIndex) -> {
//                double error = 0;
//
//                for (int i = startIndex; i < endIndex; i++) {
//                    double[][] results = evaluateSystemAtIndex(convolution, dataset, i);
//
//                    int targetIndex = (int) results[1][0];
//
//                    error += softMaxError(results[0], targetIndex);
//                }
//
//                return error;
//            }
//        );
//
//        return  forkJoinPool.invoke(task);
//    }

    @Override
    public double evaluateFitness(TrainableLayer convolution, MatrixReaderDataset dataset) {
        RecursiveEvaluation task = new RecursiveEvaluation(
            dataset,
            50,
            (startIndex, endIndex) -> {
                double fitness = 0;

                for (int i = startIndex; i < endIndex; i++) {
                    double[][] results = evaluateSystemAtIndex(convolution, dataset, i);

                    int targetIndex = (int) results[1][0];

                    fitness += accuracyFitness(results[0], targetIndex);
                }

                return fitness;
            }
        );

        return forkJoinPool.invoke(task);
    }

//    private double softMaxError(double[] predictions, int targetPosition) {
//        return - Math.log(Math.exp(predictions[targetPosition]) / sumOfE(predictions));
//    }

    private double accuracyFitness(double[] predictions, int targetPosition) {
        double truePositives = 0, trueNegatives = 0, falsePositives = 0, falseNegatives = 0;

        for (int i=0; i<predictions.length; i++) {
            int prediction = (int)Math.round(predictions[i]);

            if (i == targetPosition) {
                if (prediction == 1) {
                    truePositives++;
                }
                else {
                    falseNegatives++;
                }
            }
            else {
                if (prediction == 0) {
                    trueNegatives++;
                }
                else {
                    falsePositives++;
                }
            }
        }

        final double accuracy = (truePositives + trueNegatives) / (truePositives + falsePositives + trueNegatives + falseNegatives);
        final double truePositiveRate = truePositives / (truePositives + falseNegatives);
        final double trueNegativeRate = trueNegatives / (trueNegatives + falsePositives);
        final double positivePredictiveValue = truePositives / (truePositives + falsePositives);
        final double negativePredictiveValue = trueNegatives / (falseNegatives + trueNegatives);

        return accuracy + truePositiveRate + trueNegativeRate + positivePredictiveValue + negativePredictiveValue;
    }

    private double softMaxError(double[] predictions, int targetPosition) {
        double sum = sumOfE(predictions);
        return - Math.exp(predictions[targetPosition]) / sum + Math.exp(predictions[bestIndex(predictions)]) / sum;
    }

    private void softMax(double[] predictions) {
        double sum = sumOfE(predictions);
        for (int i = 0; i < predictions.length; i++) {
            predictions[i] = Math.exp(predictions[i]) / sum;
        }
    }

    private static double sumOfE(double[] predictions) {
        double sum = 0;
        for (int i = 0; i < predictions.length; i++) {
            sum += Math.exp(predictions[i]);
        }

        return sum;
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
        return Recombination.random(population, 1, convolutionGenes);
    }

    @Override
    public Population<TrainableLayer> selectionMethod(Population<TrainableLayer> population) {
        return Selection.tournament(population, 50, false);
    }

    @Override
    public void mutationMethod(Population<TrainableLayer> population, int epoch, int maxEpoch) {
        Mutation.mutation(population, getMutationChange(epoch), 0.5, true, convolutionGenes);
    }

    @Override
    public double[][] evaluateSystemAtIndex(TrainableLayer convolution, MatrixReaderDataset dataset, int index) {
//        ((TrainableSystem)convolution).printSchema(dataset.getData()[index]);
//        System.exit(0);
        double[] results = convolution.execute(dataset.getData()[index])[0].getRow(0);
        evaluationResult[0] = results;
        evaluationResult[1][0] = (int) dataset.getTargets()[index][0];
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
        File[] files = folder.toFile().listFiles((dir, name) -> Integer.parseInt(name) < limit);
        Arrays.sort(files, Comparator.comparingInt(o -> Integer.parseInt(o.getName())));
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

        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(file.toString())))) {
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

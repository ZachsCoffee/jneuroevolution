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
import maths.MinMax;
import multithreaded.RecursiveEvaluation;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Stl10ConvolutionProblem extends AbstractConvolution2DProblem {

    private static final ForkJoinPool forkJoinPool = new ForkJoinPool(5);

    private static final int EPOCHS = 100;

    private final ConvolutionPersonManager personManager;
    private final ConvolutionGenes convolutionGenes;

    protected Stl10ConvolutionProblem() {
        personManager = new ConvolutionPersonManager(this);
        convolutionGenes = new ConvolutionGenes();

        setDynamicMutation(new MinMax(1000, 2000), EPOCHS);

        Path basePath = Paths.get("/home/zachs/Develop/MachineLearning/stl10_binary");
        int trainLimit = 5000;
        int testLimit = 8000;
        MatrixReader[][] trainImages = readX(basePath.resolve("images/train"), trainLimit);

        trainingDataset = new MatrixReaderDataset(
            trainImages,
            readY(trainImages.length, basePath.resolve("train_y.bin"), trainLimit)
        );

        MatrixReader[][] testImages = readX(basePath.resolve("images/test"), testLimit);

        testingDataset = new MatrixReaderDataset(
            testImages,
            readY(testImages.length, basePath.resolve("test_y.bin"), testLimit)
        );
    }

    private MatrixReader[][] readX(Path folder, int limit) {
        File[] files = folder.toFile().listFiles();
        List<MatrixReader[]> data = new LinkedList<>();

        try {
            int count = 0;
            for (File file : files) {
                data.add(new RawImageInput(ImageIO.read(file)).getChannels());
                if (++count == limit) {
                    break;
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read data from the image.", e);
        }

        return data.toArray(new MatrixReader[0][]);
    }

    private double[][] readY(int targetsCount, Path file, int limit) {
        double[][] targets = new double[targetsCount][1];

        int i = 0;
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file.toString()))){
            int label;
            int count = 0;
            while ((label = inputStream.read()) != -1) {
                targets[i++][0] = label;
                if (++count == limit) {
                    break;
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read the targets.", e);
        }

        return targets;
    }

    @Override
    public NeuroevolutionPersonManager<TrainableLayer> getPersonManager() {
        return personManager;
    }

    @Override
    public TrainableLayer buildConvolution() {
        return TrainableConvolutionSystemBuilder.getInstance(3, 96, 96)
            .addConvolutionLayer()
            .setKernelsPerChannel(3)
            .and()
            .addConvolutionLayer()
            .setStride(2)
            .setKernelsPerChannel(3)
            .and()
            .addNeuralNetworkLayer()
            .addLayer(10, ActivationFunction.SIGMOID.getFunction())
            .addLayer(1, ActivationFunction.SIGMOID.getFunction())
            .and()
            .build();
    }

    @Override
    public double evaluateFitness(TrainableLayer convolution, MatrixReaderDataset dataset) {
        RecursiveEvaluation task = new RecursiveEvaluation(
            dataset,
            100,
            (startIndex, endIndex) -> {
                double error = 0;

                for (int i = startIndex; i < endIndex; i++) {
                    double[] results = evaluateSystemAtIndex(convolution, dataset, i);
                    if (results[0] != dataset.getTargets()[i][0]) {
                        error++;
                    }
                }

                return error;
            }
        );

        return dataset.getDataLength() - forkJoinPool.invoke(task);
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
        return Selection.tournament(population, 5, false);
    }

    @Override
    public void mutationMethod(Population<TrainableLayer> population, int epoch, int maxEpoch) {
        Mutation.mutation(population, getMutationChange(epoch), 2, true, convolutionGenes);
    }

    @Override
    public double[] evaluateSystemAtIndex(TrainableLayer convolution, MatrixReaderDataset dataset, int index) {
        double[] results = convolution.execute(dataset.getData()[index])[0].getRow(0);
        results[0] = Math.round(results[0] * 10);

        return new double[] {results[0], dataset.getTargets()[index][0]};
    }
}

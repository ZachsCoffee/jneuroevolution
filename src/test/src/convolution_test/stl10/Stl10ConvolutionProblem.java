package convolution_test.stl10;

import builder.ConvolutionBuilder;
import dataset.MatrixReaderDataset;
import evolution.Convolution2DProblem;
import evolution.ConvolutionGenes;
import evolution.ConvolutionPersonManager;
import evolution_builder.components.Recombination;
import evolution_builder.components.Selection;
import evolution_builder.population.Population;
import execution.NeuroevolutionPersonManager;
import execution.Problem;
import executors.common.TrainableConvolution;
import layer.MatrixReader;

public class Stl10ConvolutionProblem extends Problem<TrainableConvolution, MatrixReaderDataset> implements Convolution2DProblem<TrainableConvolution> {

    private ConvolutionPersonManager personManager;
    private ConvolutionGenes convolutionGenes;

    protected Stl10ConvolutionProblem() {
        personManager = new ConvolutionPersonManager(this);
        convolutionGenes = new ConvolutionGenes();
    }

    @Override
    public NeuroevolutionPersonManager<TrainableConvolution> getPersonManager() {
        return personManager;
    }

    @Override
    public TrainableConvolution buildConvolution() {
        return ConvolutionBuilder.getInstance(3)
            .addLayer()
            .setStride(2)
            .setKernelsPerChannel(2)
            .and()
            .build();
    }

    @Override
    public double evaluateFitness(TrainableConvolution convolution, MatrixReaderDataset dataset) {

        for (MatrixReader[] channels : dataset.getData()) {

        }
        return convolution.execute(channels.getData());
    }


    @Override
    public void computePercentOfFitness(Population<TrainableConvolution> population) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Population<TrainableConvolution> recombinationOperator(
        Population<TrainableConvolution> population,
        int epoch
    ) {
        return Recombination.random(population, 5, convolutionGenes);
    }

    @Override
    public Population<TrainableConvolution> selectionMethod(Population<TrainableConvolution> population) {
        return Selection.tournament(population, 5, false);
    }

    @Override
    public void mutationMethod(Population<TrainableConvolution> population, int epoch, int maxEpoch) {

    }
}

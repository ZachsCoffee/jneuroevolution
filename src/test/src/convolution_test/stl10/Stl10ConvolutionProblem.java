package convolution_test.stl10;

import common.TrainableSystemBuilder;
import core.layer.TrainableLayer;
import dataset.MatrixReaderDataset;
import evolution.Convolution2DProblem;
import evolution.ConvolutionGenes;
import evolution.ConvolutionPersonManager;
import evolution_builder.components.Recombination;
import evolution_builder.components.Selection;
import evolution_builder.population.Population;
import execution.NeuroevolutionPersonManager;
import execution.Problem;
import core.layer.MatrixReader;

public class Stl10ConvolutionProblem extends Problem<TrainableLayer, MatrixReaderDataset> implements Convolution2DProblem<TrainableLayer> {

    private ConvolutionPersonManager personManager;
    private ConvolutionGenes convolutionGenes;

    protected Stl10ConvolutionProblem() {
        personManager = new ConvolutionPersonManager(this);
        convolutionGenes = new ConvolutionGenes();
    }

    @Override
    public NeuroevolutionPersonManager<TrainableLayer> getPersonManager() {
        return personManager;
    }

    @Override
    public TrainableLayer buildConvolution() {
        return TrainableSystemBuilder.getInstance(3)
            .addConvolutionLayer()
            .setStride(2)
            .setKernelsPerChannel(2)
            .and()
            .build();
    }

    @Override
    public double evaluateFitness(TrainableLayer convolution, MatrixReaderDataset dataset) {

        for (MatrixReader[] channels : dataset.getData()) {
            convolution.execute(channels);
        }

        return 0;
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

    }
}

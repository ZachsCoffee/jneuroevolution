package convolution_test.stl10;

import common.TrainableSystemBuilder;
import core.layer.TrainableLayer;
import dataset.MatrixReaderDataset;
import evolution.Convolution2DProblem;
import evolution.ConvolutionGenes;
import evolution.ConvolutionPersonManager;
import evolution_builder.components.Mutation;
import evolution_builder.components.Recombination;
import evolution_builder.components.Selection;
import evolution_builder.population.Population;
import execution.NeuroevolutionPersonManager;
import execution.Problem;
import core.layer.MatrixReader;
import functions.ActivationFunction;
import maths.MinMax;

public class Stl10ConvolutionProblem extends Problem<TrainableLayer, MatrixReaderDataset> implements Convolution2DProblem<TrainableLayer> {

    private static final int EPOCHS = 100;

    private final ConvolutionPersonManager personManager;
    private final ConvolutionGenes convolutionGenes;

    protected Stl10ConvolutionProblem() {
        personManager = new ConvolutionPersonManager(this);
        convolutionGenes = new ConvolutionGenes();

        setDynamicMutation(new MinMax(1000, 2000), EPOCHS);
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
            .addNeuralNetworkLayer()
            .addLayer(10, ActivationFunction.SIGMOID.getFunction())
            .addLayer(1, ActivationFunction.SIGMOID.getFunction())
            .build();
    }

    @Override
    public double evaluateFitness(TrainableLayer convolution, MatrixReaderDataset dataset) {
        int total = dataset.getData().length;
        int errors = 0;

        for (int i=0; i<total; i++) {
            MatrixReader[] result = convolution.execute(dataset.getData()[i]);
            if (Math.round(result[0].valueAt(0, 0) * 10) != (int)dataset.getTargets()[i][0]) {
                errors++;
            }
        }

        return total - errors;
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
}

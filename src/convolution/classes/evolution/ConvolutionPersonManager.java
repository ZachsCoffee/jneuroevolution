package evolution;

import core.layer.TrainableLayer;
import data_manipulation.DatasetType;
import evolution_builder.population.PopulationPerson;
import execution.NeuroevolutionPersonManager;

import java.util.Objects;

public class ConvolutionPersonManager implements NeuroevolutionPersonManager<TrainableLayer> {

    private final Convolution2DProblem<TrainableLayer> neuroevolutionProblem;

    public ConvolutionPersonManager(Convolution2DProblem<TrainableLayer> neuroevolutionProblem) {
        this.neuroevolutionProblem = Objects.requireNonNull(neuroevolutionProblem);
    }

    @Override
    public PopulationPerson<TrainableLayer> newRandomPerson() {
        throw new UnsupportedOperationException("Random size convolution not supported.");
    }

    @Override
    public PopulationPerson<TrainableLayer> newPerson() {
        return new PopulationPerson<>(
            neuroevolutionProblem.buildConvolution()
        );
    }

    @Override
    public PopulationPerson<TrainableLayer> newSameLengthAs(PopulationPerson<TrainableLayer> populationPerson) {
        return new PopulationPerson<>(populationPerson.getGeneCode().copy());
    }

    @Override
    public double computeFitness(PopulationPerson<TrainableLayer> populationPerson, DatasetType datasetType) {
        return neuroevolutionProblem.evaluateFitness(populationPerson.getGeneCode(), neuroevolutionProblem.getDataset(datasetType));
    }
}

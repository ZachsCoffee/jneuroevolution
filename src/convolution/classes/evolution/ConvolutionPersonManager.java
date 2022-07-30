package evolution;

import data_manipulation.DatasetType;
import evolution_builder.population.PopulationPerson;
import execution.NeuroevolutionPersonManager;
import executors.common.TrainableConvolution;

import java.util.Objects;

public class ConvolutionPersonManager implements NeuroevolutionPersonManager<TrainableConvolution> {

    private final Convolution2DProblem<PopulationPerson<TrainableConvolution>> neuroevolutionProblem;

    public ConvolutionPersonManager(Convolution2DProblem<PopulationPerson<TrainableConvolution>> neuroevolutionProblem) {
        this.neuroevolutionProblem = Objects.requireNonNull(neuroevolutionProblem);
    }

    @Override
    public PopulationPerson<TrainableConvolution> newRandomPerson() {
        throw new UnsupportedOperationException("Random size convolution not supported.");
    }

    @Override
    public PopulationPerson<TrainableConvolution> newPerson() {
        return new PopulationPerson<>(
            neuroevolutionProblem.buildConvolution()
        );
    }

    @Override
    public PopulationPerson<TrainableConvolution> newSameLengthAs(PopulationPerson<TrainableConvolution> populationPerson) {
        return new PopulationPerson<>(populationPerson.getGeneCode().copy());
    }

    @Override
    public double computeFitness(PopulationPerson<TrainableConvolution> populationPerson, DatasetType datasetType) {
        return neuroevolutionProblem.evaluateFitness(populationPerson.getGeneCode(), neuroevolutionProblem.getProblem().getDataset(datasetType));
    }
}

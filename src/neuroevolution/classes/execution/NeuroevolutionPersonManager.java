package execution;

import data_manipulation.DatasetType;
import evolution_builder.population.PopulationPerson;
import evolution_builder.population.PersonManager;

public interface NeuroevolutionPersonManager<P> extends PersonManager<P> {

    @Override
    default double computeFitness(PopulationPerson<P> populationPerson) {
        throw new UnsupportedOperationException("Need to specify the rawDataset in order to compute the fitness.");
    }

    double computeFitness(PopulationPerson<P> populationPerson, DatasetType datasetType);
}

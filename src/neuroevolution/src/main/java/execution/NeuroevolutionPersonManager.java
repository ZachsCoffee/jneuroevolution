package execution;

import data_manipulation.DatasetType;
import evolution_builder.population.PopulationPerson;
import evolution_builder.population.PersonManager;

public interface NeuroevolutionPersonManager<P> extends PersonManager<P> {

    @Override
    default double computeFitness(PopulationPerson<P> populationPerson) {
        return computeFitness(populationPerson, DatasetType.TRAINING);
    }

    double computeFitness(PopulationPerson<P> populationPerson, DatasetType datasetType);
}

package execution;

import data_manipulation.DatasetType;
import evolution_builder.population.Person;
import evolution_builder.population.PersonManager;

public interface NeuroevolutionPersonManager<P> extends PersonManager<P> {

    @Override
    default double computeFitness(Person<P> person) {
        throw new UnsupportedOperationException("Need to specify the dataset in order to compute the fitness.");
    }

    double computeFitness(Person<P> person, DatasetType datasetType);
}

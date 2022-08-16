package execution.common;

import data_manipulation.DatasetTarget;
import data_manipulation.DatasetType;
import evolution_builder.components.EvolutionPhases;
import evolution_builder.population.PersonManager;

public interface CommonProblem<P, D extends DatasetTarget> extends EvolutionPhases<P> {

    PersonManager<P> getPersonManager();

    D getTrainingDataset();

    D getValidationDataset();

    D getTestingDataset();

    D getDataset(DatasetType type);
}

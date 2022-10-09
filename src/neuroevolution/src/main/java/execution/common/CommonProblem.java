package execution.common;

import data_manipulation.DatasetTarget;
import data_manipulation.DatasetType;
import evolution_builder.components.EvolutionPhases;

public interface CommonProblem<P, D extends DatasetTarget> extends EvolutionPhases<P> {

    NeuroevolutionPersonManager<P> getPersonManager();

    D getTrainingDataset();

    D getValidationDataset();

    D getTestingDataset();

    D getDataset(DatasetType type);
}

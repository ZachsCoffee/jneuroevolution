package execution;

import evolution_builder.EvolutionStage;
import evolution_builder.population.PersonMigration;
import evolution_builder.population.Population;
import evolution_builder.population.PopulationPerson;
import execution.common.ProgressListener;

import java.util.Objects;

public class GenericState<P> implements EvolutionStage<P> {
    private PersonMigration migration;
    private ProgressListener progressListener;

    public void setMigration(PersonMigration personMigration) {
        migration = Objects.requireNonNull(personMigration);
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = Objects.requireNonNull(progressListener);
    }

    @Override
    public void beforeEndEpoch(Population<P> population, int epoch) {

    }

    @Override
    public void onEndEpoch(Population<P> population, PopulationPerson<P> totalBestPopulationPerson, int epoch) {
        try {
            if (migration != null) {
                migration.migrate(population, epoch);
            }
        }
        catch (CloneNotSupportedException ex) {
            System.err.println(ex);
        }

        if (progressListener != null) {
            progressListener.epochUpdate(epoch);
        }
    }

    @Override
    public boolean stopEvolution(Population<P> population, PopulationPerson<P> totalBestPopulationPerson, int epoch) {
        PopulationPerson<P> epochBestPopulationPerson = population.getBestPerson();
        progressListener.evolutionBestUpdate(epochBestPopulationPerson.getFitness());

        return false;
    }
}

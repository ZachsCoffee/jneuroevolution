/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution;

import data_manipulation.DatasetTarget;
import data_manipulation.DatasetType;
import evolution_builder.population.PopulationPerson;
import evolution_builder.population.Population;
import evolution_builder.EvolutionStage;
import evolution_builder.population.PersonMigration;

/**
 * @author Zachs
 */
public class Stage<P, D extends DatasetTarget> implements EvolutionStage<P> {

    private final Problem<P, D> problem;
    private final double[] evolutionStatistics, validationStatistics;
    private PopulationPerson<P> validationBestPopulationPerson;
    private PersonMigration migration;
    private ProgressListener progressListener;
    private double validationMaxFitness = 0;

    public Stage(Problem<P, D> problem, int totalEpochs) {

        if (problem == null) throw new IllegalArgumentException(
            "problem not null!"
        );
        if (totalEpochs <= 0) throw new IllegalArgumentException(
            "total epochs must be positive number"
        );

//        this.totalEpochs = totalEpochs;
        this.problem = problem;

        evolutionStatistics = new double[totalEpochs];
        validationStatistics = new double[totalEpochs];
    }

    public PopulationPerson<P> getValidationBestPerson() {
        return validationBestPopulationPerson;
    }

    public double getValidationBestFitness() {
        return validationMaxFitness;
    }

    public double[] getEvolutionStatistics() {
        return evolutionStatistics;
    }

    public double[] getValidationStatistics() {
        return validationStatistics;
    }

    public void setMigration(final PersonMigration personMigration) {

        if (personMigration == null) throw new IllegalArgumentException(
            "Argument: personMigration not null"
        );

        migration = personMigration;
    }

    public void setProgressListener(ProgressListener progressListener) {

        if (progressListener == null) throw new IllegalArgumentException(
            "Argument: progressListener not null"
        );

        this.progressListener = progressListener;
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

        if (problem.getValidationDataset() != null) {
            double currentBestValidationFitness = problem.getPersonManager().computeFitness(
                epochBestPopulationPerson,
                DatasetType.VALIDATION
            );
            if (currentBestValidationFitness > validationMaxFitness) {
                validationMaxFitness = currentBestValidationFitness;
                validationBestPopulationPerson = population.getBestPerson();
            }

            validationStatistics[epoch] = currentBestValidationFitness;
        }

        progressListener.evolutionBestUpdate(epochBestPopulationPerson.getFitness());

        evolutionStatistics[epoch] = epochBestPopulationPerson.getFitness();

        return false;
    }

    public interface ProgressListener {

        void epochUpdate(int currentEpoch);

        void evolutionBestUpdate(double bestPersonFitness);
    }
}

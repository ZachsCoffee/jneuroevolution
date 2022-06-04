/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution;

import data_manipulation.DatasetType;
import evolution_builder.population.Person;
import evolution_builder.population.Population;
import evolution_builder.EvolutionStage;
import evolution_builder.population.PersonMigration;

/**
 *
 * @author Zachs
 */
public class Stage<P> implements EvolutionStage<P> {

    private Person<P> validationBestPerson;
    
    private PersonMigration migration;
    private Problem<P> problem;
    private ProgressListener progressListener;
    
    private double[] evolutionStatistics, validationStatistics;
            
    private double validationMaxFitness = 0;
    
    public Stage(Problem<P> problem, int totalEpochs) {
        
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
    
    public void setMigration(final PersonMigration personMigration) {
        
        if (personMigration == null) throw new IllegalArgumentException(
            "Argument: personMigration not null"
        );
        
        migration = personMigration;
    }

    public Person<P> getValidationBestPerson() {
        return validationBestPerson;
    }
    public double getValidationBestFitness(){
        return validationMaxFitness;
    }
    public double[] getEvolutionStatistics() {
        return evolutionStatistics;
    }

    public double[] getValidationStatistics() {
        return validationStatistics;
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
    public void onEndEpoch(Population<P> population, Person<P> totalBestPerson, int epoch) {
        
        try {
            if (migration != null){
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
    public boolean stopEvolution(Population<P> population, Person<P> totalBestPerson, int epoch) {
        Person<P> epochBestPerson = population.getBestPerson();

        if (problem.getValidationDataset() != null) {
            double currentBestValidationFitness = problem.getPersonManager().computeFitness(epochBestPerson, DatasetType.VALIDATION);
            if (currentBestValidationFitness > validationMaxFitness){
                validationMaxFitness = currentBestValidationFitness;
                validationBestPerson = population.getBestPerson();
            }

            validationStatistics[epoch] = currentBestValidationFitness;
        }

        progressListener.evolutionBestUpdate(epochBestPerson.getFitness());
        
        evolutionStatistics[epoch] = epochBestPerson.getFitness();

        return false;
    }

    public interface ProgressListener {
        void epochUpdate(int currentEpoch);
        
        void evolutionBestUpdate(double bestPersonFitness);
    }
}

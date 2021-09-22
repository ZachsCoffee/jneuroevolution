/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import evolution_builder.population.Population;
import evolution_builder.EvolutionStage;
import evolution_builder.population.PersonMigration;

/**
 *
 * @author Zachs
 */
public class Stage implements EvolutionStage {

    private evolution_builder.population.Person validationBestPerson;
    
    private PersonMigration migration;
    private Problem problem;
    private ProgressListener progressListener;
    
    private float[] evolutionStatistics;
    private float[] validationStatistics;
            
    private double validationMaxFitness = 0;
    
    public Stage(Problem problem, int totalEpochs) {
        
        if (problem == null) throw new IllegalArgumentException(
                "problem not null!"
        );
        if (totalEpochs <= 0) throw new IllegalArgumentException(
                "total epochs must be positive number"
        );
        
//        this.totalEpochs = totalEpochs;
        this.problem = problem;

        evolutionStatistics = new float[totalEpochs];
        validationStatistics = new float[totalEpochs];
    }
    
    public void setMigration(final PersonMigration personMigration) {
        
        if (personMigration == null) throw new IllegalArgumentException(
            "Argument: personMigration not null"
        );
        
        migration = personMigration;
    }

    public evolution_builder.population.Person getValidationBestPerson() {
        return validationBestPerson;
    }
    public double getValidationBestFitness(){
        return validationMaxFitness;
    }
    public float[] getEvolutionStatistics() {
        return evolutionStatistics;
    }

    public float[] getValidationStatistics() {
        return validationStatistics;
    }
    
    public void setProgressListener(ProgressListener progressListener) {
        
        if (progressListener == null) throw new IllegalArgumentException(
            "Argument: progressListener not null"
        );
        
        this.progressListener = progressListener;
    }
    
    
    @Override
    public void beforeEndEpoch(Population population, int epoch) {

    }
    
    @Override
    public void onEndEpoch(Population population, evolution_builder.population.Person totalBestPerson, int epoch) {
        
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
    public boolean stopEvolution(Population population, evolution_builder.population.Person totalBestPerson, int epoch) {
        float
                validationCurrentFitness;
        float validationEpochBestFitness = 0;
        float evolutionCurrentFitness;
        float evolutionEpochBestFitness = 0;

        int populationSize = population.getSize();
        evolution_builder.population.Person currentPerson;
        for (int i=0; i<populationSize; i++){
            
            currentPerson = population.getPersonAt(i);
            evolutionCurrentFitness = currentPerson.getFitness();
            validationCurrentFitness = Person.computeFitness(currentPerson, problem.getValidationDataset());

            if (evolutionCurrentFitness > evolutionEpochBestFitness) {
                evolutionEpochBestFitness = evolutionCurrentFitness;
            }

            if (validationCurrentFitness > validationMaxFitness){
                validationMaxFitness = validationCurrentFitness;
                validationBestPerson = currentPerson;
            }
            if (validationCurrentFitness > validationEpochBestFitness){
                validationEpochBestFitness = validationCurrentFitness;
            }
            
        }
        
        progressListener.evolutionBestUpdate(evolutionEpochBestFitness);
        
        evolutionStatistics[epoch] = evolutionEpochBestFitness;
        validationStatistics[epoch] = validationEpochBestFitness;

        return false;
    }

    public interface ProgressListener {
        void epochUpdate(int currentEpoch);
        
        void evolutionBestUpdate(double bestPersonFitness);
    }
}

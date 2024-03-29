/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder;

import evolution_builder.components.EvolutionPhases;
import evolution_builder.population.Person;
import evolution_builder.population.Population;

import java.util.Objects;

/**
 *
 * @author main
 */
public class Evolution {
    private final EvolutionPhases components;
    private final boolean withPercentOfFitness;

    private Population population;
    private Person totalBestPerson = null;
    private boolean earlyStopped = false;
    
    public Evolution(Population population, boolean withPercentOfFitness, EvolutionPhases components) {
        Objects.requireNonNull(components);
        Objects.requireNonNull(population);
        
        this.population = population;
        this.components = components;
        this.withPercentOfFitness = withPercentOfFitness;
    }
    
    public Person getTotalBestPerson(){
        return totalBestPerson;
    }

    
    public boolean withPercentOfFitness(){
        return withPercentOfFitness;
    }
    
    public boolean earlyStopped(){
        return earlyStopped;
    }
    
    public Population startEvolution(int epochs){
        if (epochs < 1){
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than one.");
        }
        
        if (withPercentOfFitness){
            computeFitnessForPopulation();
            components.computePercentOfFitness(population);

            for (int i=0; i<epochs; i++){ 
                population = components.selectionMethod(population);
                population = components.recombinationOperator(population, i);
                components.mutationMethod(population, i, epochs);

                computeFitnessForPopulation();
                components.computePercentOfFitness(population);
            }
        }
        else{
            computeFitnessForPopulation();

            for (int i=0; i<epochs; i++){ 
                population = components.selectionMethod(population);
                population = components.recombinationOperator(population, i);
                components.mutationMethod(population, i, epochs);

                computeFitnessForPopulation();
            }
        }
        return population;
    }
    
    public Population startEvolution(int epochs, EvolutionStage stage){
        Objects.requireNonNull(stage);

        if (epochs < 1){
            throw new IllegalArgumentException("Epochs can't be smaller than 1.");
        }
        
        if (withPercentOfFitness){
            computeFitnessForPopulation();

            components.computePercentOfFitness(population);
            for (int i=0; i<epochs; i++){
                population = components.selectionMethod(population);
                population = components.recombinationOperator(population, i);
                components.mutationMethod(population, i, epochs);
                
                stage.beforeEndEpoch(population, i);

                computeFitnessForPopulation();
                components.computePercentOfFitness(population);
                
                stage.onEndEpoch(population, totalBestPerson, i);

                if (earlyStopped = stage.stopEvolution(population, totalBestPerson, i)){
                    break;
                }
            }
//            lastRunningTime = System.nanoTime() - first;
        }
        else{
//            long first = System.nanoTime();
            computeFitnessForPopulation();
            
            for (int i=0; i<epochs; i++){
                population = components.selectionMethod(population);
                population = components.recombinationOperator(population, i);
                components.mutationMethod(population, i, epochs);
                
                stage.beforeEndEpoch(population, i);

                computeFitnessForPopulation();
                
                stage.onEndEpoch(population, totalBestPerson, i);

                if (stage.stopEvolution(population, totalBestPerson, i)){
                    break;
                }
            }
//            lastRunningTime = System.nanoTime() - first;
        }
        
        return population;
    }
    
    public void reComputeFitness(){
        computeFitnessForPopulation();
    }

    private void computeFitnessForPopulation() {
        population.computeFitnessForPopulation();

        Person populationBest = population.getBestPerson();

        if (totalBestPerson == null) {
            totalBestPerson = populationBest;
        }
        else if (populationBest.getFitness() > totalBestPerson.getFitness()) {
            totalBestPerson = populationBest;
        }
    }
}

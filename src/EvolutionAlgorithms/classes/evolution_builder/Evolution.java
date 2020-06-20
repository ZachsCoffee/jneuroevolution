/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder;

import evolution_builder.components.EvolutionComponents;
import evolution_builder.population.Person;
import evolution_builder.population.Population;

/**
 *
 * @author main
 */
public class Evolution {
    private EvolutionComponents components;
    private Population population;
    private Person totalBestPerson = null;
//    private long lastRunningTime = -1;
    private boolean withPercentOfFitness, earlyStoped = false;
    
    public Evolution(Population population, boolean withPercentOfFitness, EvolutionComponents components) {
        if (components == null){
            throw new IllegalArgumentException("Argument, at pos 2: not null.");
        }
        
        if (population == null){
            throw new IllegalArgumentException("Argument, at pos 1: not null.");
        }
        
        this.population = population;
        this.components = components;
        this.withPercentOfFitness = withPercentOfFitness;
    }
    
    public Person getTotalBestPerson(){
        return totalBestPerson;
    }
    
//    public long getLastRunningTime(){
//        return lastRunningTime;
//    }
    
    public boolean withPercentOfFitness(){
        return withPercentOfFitness;
    }
    
    public boolean earlyStoped(){
        return earlyStoped;
    }
    
    public Population startEvolution(int epochs){
        if (epochs < 1){
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than one.");
        }
        
        if (withPercentOfFitness){
            totalBestPerson = population.computeFitnessForPopulation(totalBestPerson);
            components.computePercentOfFitness(population);

            for (int i=0; i<epochs; i++){ 
                population = components.selectionMethod(population);
                population = components.recombinationOperator(population, i);
                components.mutationMethod(population, i, epochs);

                totalBestPerson = population.computeFitnessForPopulation(totalBestPerson);
                components.computePercentOfFitness(population);
            }
        }
        else{
            totalBestPerson = population.computeFitnessForPopulation(totalBestPerson);

            for (int i=0; i<epochs; i++){ 
                population = components.selectionMethod(population);
                population = components.recombinationOperator(population, i);
                components.mutationMethod(population, i, epochs);

                totalBestPerson = population.computeFitnessForPopulation(totalBestPerson);
            }
        }
        return population;
    }
    
    public Population startEvolution(int epochs, EvolutionStage stage){
        if (epochs < 1){
            throw new IllegalArgumentException("Argument, at pos 1: must be greater than one.");
        }
        if (stage == null){
            throw new IllegalArgumentException("Argument, at pos 2: not null.");
        }
        
        if (withPercentOfFitness){
//            long first = System.nanoTime();
            totalBestPerson = population.computeFitnessForPopulation(totalBestPerson);
            components.computePercentOfFitness(population);
            for (int i=0; i<epochs; i++){
                population = components.selectionMethod(population);
                population = components.recombinationOperator(population, i);
                components.mutationMethod(population, i, epochs);
                
                stage.beforeEndEpoch(population, i);
                
                totalBestPerson = population.computeFitnessForPopulation(totalBestPerson);
                components.computePercentOfFitness(population);
                
                stage.onEndEpoch(population, totalBestPerson, i);

                if (earlyStoped = stage.stopEvolution(population, totalBestPerson, i)){
                    break;
                }
            }
//            lastRunningTime = System.nanoTime() - first;
        }
        else{
//            long first = System.nanoTime();
            totalBestPerson = population.computeFitnessForPopulation(totalBestPerson);
            
            for (int i=0; i<epochs; i++){
                population = components.selectionMethod(population);
                population = components.recombinationOperator(population, i);
                components.mutationMethod(population, i, epochs);
                
                stage.beforeEndEpoch(population, i);
                
                totalBestPerson = population.computeFitnessForPopulation(totalBestPerson);
                
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
        totalBestPerson = population.computeFitnessForPopulation(totalBestPerson);
    }
}

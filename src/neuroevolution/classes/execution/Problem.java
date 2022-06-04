/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution;

import data_manipulation.Dataset;
import evolution_builder.components.EvolutionPhases;
import maths.Function;
import maths.LinearValues;
import maths.MinMax;

import java.util.Objects;

/**
 *
 * @author Zachs
 */
public abstract class Problem<P> implements EvolutionPhases<P> {
    
    protected Function hiddenLayerFunction, outputLayerFunction;
    protected Dataset 
            trainingDataset,
            validationDataset,
            testingDataset;
    
    private LinearValues dynamicMutation = null;
    private int fixedMutation;
    
    protected final NeuroevolutionPersonManager<P> personManager;
    
    protected Problem(NeuroevolutionPersonManager<P> personManager){
        this.personManager = Objects.requireNonNull(personManager);
    }
    
    public void setFixedMutation(int mutation){
        if (mutation <= 0) throw new IllegalArgumentException("Muation must be grater than zero");
        
        fixedMutation = mutation;
    }
    public final void setDynamicMutation(MinMax mutationValues, int epochs){
        dynamicMutation = new LinearValues(mutationValues, epochs, LinearValues.Order.DESC);
    }
    
    public int getMutationChange(int currentEpoch){
        if (dynamicMutation != null){
            return dynamicMutation.compute(currentEpoch);
        }
        else {
            return fixedMutation;
        }
    }
    
    public NeuroevolutionPersonManager<P> getPersonManager(){
        System.out.println(Thread.currentThread().getId());
        return personManager;
    }

    public Dataset getTrainingDataset() {
        System.out.println(Thread.currentThread().getId());

        return trainingDataset;
    }

    public Dataset getValidationDataset() {
                System.out.println(Thread.currentThread().getId());

        return validationDataset;
    }

    public Dataset getTestingDataset() {
                System.out.println(Thread.currentThread().getId());

        return testingDataset;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import data_manipulation.Dataset;
import evolution_builder.components.EvolutionComponents;
import evolution_builder.population.PersonManager;
import maths.Function;
import maths.LinearValues;
import maths.MinMax;

/**
 *
 * @author Zachs
 */
public abstract class Problem implements MLProblem, EvolutionComponents {
    
    protected Function hiddenLayerFunction, outputLayerFunction;
    protected Dataset 
            trainingDataset,
            validationDataset,
            testingDataset;
    
    private LinearValues dynamicMutation = null;
    private int fixedMutation;
    
    protected final Person PERSON;
    
    protected Problem(){
        PERSON = new Person(this);
    }

    public Person getPerson(){
        return PERSON;
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
        else{
            return fixedMutation;
        }
    }
    
    public PersonManager getPersonManager(){
        return PERSON;
    }

    public Dataset getTrainingDataset() {
        return trainingDataset;
    }

    public Dataset getValidationDataset() {
        return validationDataset;
    }

    public Dataset getTestingDataset() {
        return testingDataset;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder;

/**
 *
 * @author Zachs
 */
public class EvolutionOptions {
    public enum PercentOfFitness {
        RANKED, CURRENT_BEST, TOTAL_BEST
    }
    public enum RecombinationOperator {
        RANDOM, FIXED, RANDOM_WITH_FILTER, FIXED_WITH_FILTER
    }
    public enum Selection {
        TOURNAMENT, ROULETTE
    }

    private int epochs = 100;
    
    // MUTATION
    private int mutationValueRange = 2;
    private boolean withNegative = true;
    private int mutationChance = 1000;
    
    // WITH PERCENT OF FITNESS
    private boolean withPercentOfFitness = false;
    private PercentOfFitness percentOfFitnessMethod;
    private double totalBest = 0;
    
    // METHODS
    private RecombinationOperator recombinationOperator;
    private Selection selectionMethod;
    
//    public EvolutionOptions withPercentOfFitness(PercentOfFitness percentOfFitness){
//        
//    }
//    public EvolutionOptions withPercentOfFitness(PercentOfFitness percentOfFitness, double totalBest){
//        
//    }
}

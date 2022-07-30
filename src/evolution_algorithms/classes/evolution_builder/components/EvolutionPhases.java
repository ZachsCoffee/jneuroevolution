/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.components;

import evolution_builder.population.Population;

/**
 *
 * @author main
 */
public interface EvolutionPhases<P> {
    void computePercentOfFitness(Population<P> population);
    
    Population<P> recombinationOperator(Population<P> population, int epoch);
    
    Population<P> selectionMethod(Population<P> population);
    
    void mutationMethod(Population<P> population, int epoch, int maxEpoch);
}

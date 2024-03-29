/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.components;

import evolution_builder.population.Person;
import evolution_builder.population.Population;

/**
 *
 * @author main
 */
public interface EvolutionPhases {
    void computePercentOfFitness(Population population);
    
    Population recombinationOperator(Population population, int epoch);
    
    Population selectionMethod(Population population);
    
    void mutationMethod(Population population, int epoch, int maxEpoch);
}

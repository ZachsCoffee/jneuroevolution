/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder;

import evolution_builder.population.PopulationPerson;
import evolution_builder.population.Population;

/**
 *
 * @author main
 */
public interface EvolutionStage<P> {
    /**
     * Just before compute population fitness. Use this method, to write a hybrid evolution 
     * @param population The population of current evolution
     * @param epoch The current epoch, starts from 1
     */
    void beforeEndEpoch(Population<P> population, int epoch);
    
    /**
     * Here you can keep an eye in the evolution
     * @param population The population of current evolution
     * @param totalBestPopulationPerson The best person, from all epoch include this one
     * @param epoch The current epoch, starts from 1
     */
    void onEndEpoch(Population<P> population, PopulationPerson<P> totalBestPopulationPerson, int epoch);
    
    /**
     * Use this method if you want to stop the evolution
     * @param population The population of current evolution
     * @param totalBestPopulationPerson The best person, from all epoch include this one
     * @param epoch The current epoch, starts from 1
     * @return If true the evolution will stop.
     */
    boolean stopEvolution(Population<P> population, PopulationPerson<P> totalBestPopulationPerson, int epoch);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.population;

/**
 * @author main
 */
public interface Genes<T, P> {

    T getGenAt(PopulationPerson<P> populationPerson, int position);

    void setGenAt(PopulationPerson<P> populationPerson, T gene, int position);

    void mutationValue(PopulationPerson<P> populationPerson, int position, double mutationValue);

    int genesCount(PopulationPerson<P> populationPerson);
}

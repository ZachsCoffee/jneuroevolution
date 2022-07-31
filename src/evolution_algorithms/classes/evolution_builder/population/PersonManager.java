/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.population;

/**
 *
 * @author main
 */
public interface PersonManager<P> {
    
    PopulationPerson<P> newRandomPerson();

    /**
     * This method allow evolution to create a new person.
     * @return Must return a new instance of a person.
     */
    PopulationPerson<P> newPerson();
    
    /**
     * Creates a person same length as the given person. This is used when the person's length is variable
     * @param populationPerson The given person to create a new with the same length
     * @return The new person
     */
    
    PopulationPerson<P> newSameLengthAs(PopulationPerson<P> populationPerson);
    
    
    double computeFitness(PopulationPerson<P> populationPerson);

    P getGenes();
}

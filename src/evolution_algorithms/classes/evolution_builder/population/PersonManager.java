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
    
    Person<P> newRandomPerson();

    /**
     * This method allow evolution to create a new person.
     * @return Must return a new instance of a person.
     */
    Person<P> newPerson();
    
    /**
     * Creates a person same length as the given person. This is used when the person's length is variable
     * @param person The given person to create a new with the same length
     * @return The new person
     */
    
    Person<P> newSameLengthAs(Person<P> person);
    
    
    double computeFitness(Person<P> person);
}

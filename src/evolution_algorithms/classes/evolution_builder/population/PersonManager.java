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
public interface PersonManager {
    public Person newRandomPerson();
    
    //gia na mporei na balei neo person
    public Person newPerson();
    
    /**
     * Creates a person same length as the given person. This is used when the persons length is variable
     * @param person The given person to create a new with the same length
     * @return The new person
     */
    public Person newSameLengthAs(Person person);
    
    public double computeFitness(Person person);
}

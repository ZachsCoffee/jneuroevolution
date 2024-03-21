/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.population;

import evolution_builder.population.Person;

/**
 *
 * @author main
 */
public interface Genes <T>{
    public T getGenAt(Person person, int position);
    
    public void setGenAt(Person person, T gene, int position);
    
    public void mutationValue(Person person, int position, double mutationValue);
    
    public int genesCount(Person person);
}

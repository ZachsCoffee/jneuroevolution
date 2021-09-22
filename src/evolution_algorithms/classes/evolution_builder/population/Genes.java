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
public interface Genes <T>{
    T getGenAt(Person person, int position);
    
    void setGenAt(Person person, T gene, int position);
    
    void mutationValue(Person person, int position, double mutationValue);

    int genesCount(Person person);
}

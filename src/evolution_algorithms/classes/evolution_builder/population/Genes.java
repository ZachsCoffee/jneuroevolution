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

    T getGenAt(Person<P> person, int position);

    void setGenAt(Person<P> person, T gene, int position);

    void mutationValue(Person<P> person, int position, double mutationValue);

    int genesCount(Person<P> person);
}

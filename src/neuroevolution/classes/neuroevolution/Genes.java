/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import networks.interfaces.Network;
import evolution_builder.population.Person;

/**
 *
 * @author Zachs
 */
class Genes implements evolution_builder.population.Genes<Double>{
    static int maxStartValue = 1;
    
    @Override
    public Double getGenAt(Person person, int position) {
        return ((Network) person.getGeneCode()).getWeightAt(position);
    }

    @Override
    public void setGenAt(Person person, Double gene, int position) {
        ((Network) person.getGeneCode()).setWeightAt(position, gene);
    }

    @Override
    public void mutationValue(Person person, int position, double mutationValue) {
        Network network = (Network) person.getGeneCode();
        double weight = network.getWeightAt(position);
        if (weight + mutationValue > maxStartValue){
            maxStartValue = (int)(weight + mutationValue);
        }
        network.setWeightAt(position, weight + mutationValue);
    }

    @Override
    public int genesCount(Person person) {
        return ((Network) person.getGeneCode()).getWeightsCount();
    }
    
}

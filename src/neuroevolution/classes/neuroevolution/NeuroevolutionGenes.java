/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import evolution_builder.population.Genes;
import networks.interfaces.Network;
import evolution_builder.population.Person;

/**
 *
 * @author Zachs
 */
class NeuroevolutionGenes implements Genes<Double, Network> {
    static int maxStartValue = 1;
    
    @Override
    public Double getGenAt(Person<Network> person, int position) {
        return person.getGeneCode().getWeightAt(position);
    }

    @Override
    public void setGenAt(Person<Network> person, Double gene, int position) {
        person.getGeneCode().setWeightAt(position, gene);
    }

    @Override
    public void mutationValue(Person<Network> person, int position, double mutationValue) {
        Network network = person.getGeneCode();
        double weight = network.getWeightAt(position);
        if (weight + mutationValue > maxStartValue){
            maxStartValue = (int)(weight + mutationValue);
        }
        network.setWeightAt(position, weight + mutationValue);
    }

    @Override
    public int genesCount(Person<Network> person) {
        return person.getGeneCode().getWeightsCount();
    }
    
}

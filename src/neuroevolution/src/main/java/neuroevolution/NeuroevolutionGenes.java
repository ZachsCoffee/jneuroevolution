/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import evolution_builder.population.Genes;
import networks.interfaces.Network;
import evolution_builder.population.PopulationPerson;

/**
 *
 * @author Zachs
 */
class NeuroevolutionGenes implements Genes<Double, Network> {
    static int maxStartValue = 1;
    
    @Override
    public Double getGenAt(PopulationPerson<Network> populationPerson, int position) {
        return populationPerson.getGeneCode().getWeightAt(position);
    }

    @Override
    public void setGenAt(PopulationPerson<Network> populationPerson, Double gene, int position) {
        populationPerson.getGeneCode().setWeightAt(position, gene);
    }

    @Override
    public void mutationValue(PopulationPerson<Network> populationPerson, int position, double mutationValue) {
        Network network = populationPerson.getGeneCode();
        double weight = network.getWeightAt(position);
        if (weight + mutationValue > maxStartValue){
            maxStartValue = (int)(weight + mutationValue);
        }
        network.setWeightAt(position, weight + mutationValue);
    }

    @Override
    public int genesCount(PopulationPerson<Network> populationPerson) {
        return populationPerson.getGeneCode().getTotalWeights();
    }
    
}

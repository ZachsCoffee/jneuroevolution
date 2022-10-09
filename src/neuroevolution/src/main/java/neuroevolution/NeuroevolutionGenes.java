/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import core.layer.TrainableLayer;
import evolution_builder.population.Genes;
import networks.interfaces.Network;
import evolution_builder.population.PopulationPerson;

/**
 *
 * @author Zachs
 */
public class NeuroevolutionGenes<L extends TrainableLayer> implements Genes<Double, L> {
    static int maxStartValue = 1;

    @Override
    public Double getGenAt(PopulationPerson<L> populationPerson, int position) {
        return populationPerson.getGeneCode().getWeightAt(position);
    }

    @Override
    public void setGenAt(PopulationPerson<L> populationPerson, Double gene, int position) {
        populationPerson.getGeneCode().setWeightAt(position, gene);
    }

    @Override
    public void mutationValue(PopulationPerson<L> populationPerson, int position, double mutationValue) {
        L network = populationPerson.getGeneCode();
        double weight = network.getWeightAt(position);
        if (weight + mutationValue > maxStartValue){
            maxStartValue = (int)(weight + mutationValue);
        }
        network.setWeightAt(position, weight + mutationValue);
    }

    @Override
    public int genesCount(PopulationPerson<L> populationPerson) {
        return populationPerson.getGeneCode().getTotalWeights();
    }
}

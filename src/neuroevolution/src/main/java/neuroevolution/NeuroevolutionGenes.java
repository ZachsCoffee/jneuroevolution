/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import evolution_builder.population.Genes;
import evolution_builder.population.PopulationPerson;
import networks.interfaces.PartialNetwork;

/**
 * @author Zachs
 */
public class NeuroevolutionGenes<L extends PartialNetwork> implements Genes<Double, L> {

    static int maxStartValue = 1;

    private final NeuroevolutionGenesOptions neuroevolutionGenesOptions;

    public NeuroevolutionGenes() {
        this(NeuroevolutionGenesOptions.Builder.getInstance().build());
    }

    public NeuroevolutionGenes(NeuroevolutionGenesOptions neuroevolutionGenesOptions) {
        this.neuroevolutionGenesOptions = neuroevolutionGenesOptions;
    }

    @Override
    public Double getGenAt(PopulationPerson<L> populationPerson, int position) {
        return populationPerson.getGeneCode().getWeightAt(position);
    }

    @Override
    public void setGenAt(PopulationPerson<L> populationPerson, Double gene, int position) {
        populationPerson.getGeneCode().setWeightAt(position, gene);
    }

    @Override
    public void mutationValue(
        PopulationPerson<L> populationPerson,
        int index,
        double mutationValue,
        double maxMutationValue,
        boolean withNegative
    ) {
        L network = populationPerson.getGeneCode();

        if (neuroevolutionGenesOptions.hasDeactivationChange()) {
            if (mutationValue <= maxMutationValue * neuroevolutionGenesOptions.getWeightStatusMutationChance()) {
                network.reverseWeightStatus(index);
            }
            else {
                mutateWeight(network, index, mutationValue);
            }
        }
        else {
            mutateWeight(network, index, mutationValue);
        }
    }

    private void mutateWeight(L network, int index, double mutationValue) {
        double weight = network.getWeightAt(index);
        if (weight + mutationValue > maxStartValue) {
            maxStartValue = (int) (weight + mutationValue);
        }
        network.setWeightAt(index, weight + mutationValue);
    }

    @Override
    public int genesCount(PopulationPerson<L> populationPerson) {
        return populationPerson.getGeneCode().getTotalWeights();
    }
}

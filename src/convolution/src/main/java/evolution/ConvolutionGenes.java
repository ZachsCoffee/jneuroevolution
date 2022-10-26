package evolution;

import core.layer.TrainableLayer;
import evolution_builder.population.Genes;
import evolution_builder.population.PopulationPerson;

public class ConvolutionGenes implements Genes<Double, TrainableLayer> {

    @Override
    public Double getGenAt(PopulationPerson<TrainableLayer> populationPerson, int position) {
        return populationPerson.getGeneCode().getWeightAt(position);
    }

    @Override
    public void setGenAt(PopulationPerson<TrainableLayer> populationPerson, Double gene, int position) {
        populationPerson.getGeneCode().setWeightAt(position, gene);
    }

    @Override
    public void mutationValue(
        PopulationPerson<TrainableLayer> populationPerson,
        int index,
        double mutationValue,
        double maxMutationValue,
        boolean withNegative
    ) {
        populationPerson.getGeneCode().setWeightAt(index, mutationValue);
    }

    @Override
    public int genesCount(PopulationPerson<TrainableLayer> populationPerson) {
        return populationPerson.getGeneCode().getTotalWeights();
    }
}

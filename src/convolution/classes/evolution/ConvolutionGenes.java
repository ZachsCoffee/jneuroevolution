package evolution;

import evolution_builder.population.Genes;
import evolution_builder.population.PopulationPerson;
import executors.common.TrainableConvolution;

public class ConvolutionGenes implements Genes<Double, TrainableConvolution> {

    @Override
    public Double getGenAt(PopulationPerson<TrainableConvolution> populationPerson, int position) {
        return populationPerson.getGeneCode().getWeightAt(position);
    }

    @Override
    public void setGenAt(PopulationPerson<TrainableConvolution> populationPerson, Double gene, int position) {
        populationPerson.getGeneCode().setWeightAt(position, gene);
    }

    @Override
    public void mutationValue(PopulationPerson<TrainableConvolution> populationPerson, int position, double mutationValue) {
        populationPerson.getGeneCode().setWeightAt(position, mutationValue);
    }

    @Override
    public int genesCount(PopulationPerson<TrainableConvolution> populationPerson) {
        return populationPerson.getGeneCode().getTotalWeights();
    }
}

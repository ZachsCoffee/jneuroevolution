package evolution;

import evolution_builder.population.Genes;
import evolution_builder.population.Person;
import networks.interfaces.Network;

public class ConvolutionGenes implements Genes<Double, Network> {

    @Override
    public Double getGenAt(Person<Network> person, int position) {
        return null;
    }

    @Override
    public void setGenAt(Person<Network> person, Double gene, int position) {

    }

    @Override
    public void mutationValue(Person<Network> person, int position, double mutationValue) {

    }

    @Override
    public int genesCount(Person<Network> person) {
        return 0;
    }
}

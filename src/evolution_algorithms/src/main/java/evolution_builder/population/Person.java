package evolution_builder.population;

public interface Person<P> extends Comparable<PopulationPerson<P>> {
    P getGeneCode();

    double getFitness();
}

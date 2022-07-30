package evolution_builder.population;

public interface Person<P> extends Comparable<PopulationPerson<P>>, Cloneable{
    P getGeneCode();

    double getFitness();
}

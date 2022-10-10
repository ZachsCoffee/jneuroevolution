/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.population;


/**
 * @author main
 */
public class PopulationPerson<T> implements Person<T> {

    /**
     * Copy a person Object and returns it
     *
     * @param populationPerson The person to copy
     * @return The new object of person
     */
    public static <T> PopulationPerson<T> copyPerson(PopulationPerson<T> populationPerson) {
        PopulationPerson<T> p = new PopulationPerson<>(populationPerson.geneCode, populationPerson.fitness);
        p.percentOfFitness = populationPerson.percentOfFitness;
        return p;
    }

    private T geneCode;
    private double fitness;
    private int percentOfFitness;

    /**
     * Creates a person for the population
     *
     * @param geneCode The object that represents a person at the population
     */
    public PopulationPerson(T geneCode) {

        if (geneCode == null) throw new IllegalArgumentException(
            "object person not null"
        );

        this.geneCode = geneCode;
    }

    /**
     * Creates a person for the population
     *
     * @param geneCode The object that represents a person at the population
     * @param fitness  The fitness of person
     */
    public PopulationPerson(T geneCode, double fitness) {
        this(geneCode);

        setFitness(fitness);
    }

    /**
     * Creates a person for the population
     *
     * @param geneCode             The object that represents a person at the population
     * @param allowNegativeFitness Allow negative values for person fitness. Default is false
     */
    public PopulationPerson(T geneCode, boolean allowNegativeFitness) {
        if (geneCode == null) throw new IllegalArgumentException(
            "object person not null"
        );

        this.geneCode = geneCode;
    }

    /**
     * Creates a person for the population
     *
     * @param geneCode             The object that represents a person at the population
     * @param fitness              The fitness of person
     * @param allowNegativeFitness Allow negative values for person fitness. Default is false
     */
    public PopulationPerson(T geneCode, double fitness, boolean allowNegativeFitness) {
        this(geneCode, allowNegativeFitness);

        setFitness(fitness);
    }

    public int getPercentOfFitness() {
        return percentOfFitness;
    }

    /**
     * Setter for the percentOfFitness
     *
     * @param percentOfFitness The percent range [0-100]
     * @throws IllegalArgumentException If the percent value is negative
     */
    public void setPercentOfFitness(int percentOfFitness) {
        if (percentOfFitness < 0)
            throw new IllegalArgumentException("percent of fitness can't be negative percentOfFitness=" + percentOfFitness);

        this.percentOfFitness = percentOfFitness;
    }

    public T getGeneCode() {
        return geneCode;
    }

    public void setGeneCode(T geneCode) {
        if (geneCode == null) {
            throw new IllegalArgumentException("Argument, at pos 1: not null.");
        }

        this.geneCode = geneCode;
    }

    public double getFitness() {
        return fitness;
    }

    final void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public int compareTo(PopulationPerson otherPopulationPerson) {
        return Double.compare(fitness, otherPopulationPerson.fitness);
    }

    public String toString() {
        return "Person: " + geneCode + "\nFitness: " + fitness + "\nPercent of fitness: " + percentOfFitness;
    }
}

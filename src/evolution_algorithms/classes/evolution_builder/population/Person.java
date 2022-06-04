/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.population;


/**
 * @author main
 */
public class Person<T> implements Comparable<Person<T>>, Cloneable {

    /**
     * Copy a person Object and returns it
     *
     * @param person The person to copy
     * @return The new object of person
     */
    public static <T> Person<T> copyPerson(Person<T> person) {
        Person<T> p = new Person<>(person.person, person.fitness);
        p.percentOfFitness = person.percentOfFitness;
        return p;
    }
    private final boolean ALLOW_NEGATIVE_FITNESS;
    private T person;
    private double fitness;
    private int percentOfFitness;

    /**
     * Creates a person for the population
     *
     * @param person The object that represents a person at the population
     */
    public Person(T person) {

        if (person == null) throw new IllegalArgumentException(
            "object person not null"
        );

        this.person = person;

        ALLOW_NEGATIVE_FITNESS = false;
    }

    /**
     * Creates a person for the population
     *
     * @param person  The object that represents a person at the population
     * @param fitness The fitness of person
     */
    public Person(T person, double fitness) {
        this(person);

        setFitness(fitness);
    }

    /**
     * Creates a person for the population
     *
     * @param person               The object that represents a person at the population
     * @param allowNegativeFitness Allow negative values for person fitness. Default is false
     */
    public Person(T person, boolean allowNegativeFitness) {
        if (person == null) throw new IllegalArgumentException(
            "object person not null"
        );

        this.person = person;

        ALLOW_NEGATIVE_FITNESS = allowNegativeFitness;
    }

    /**
     * Creates a person for the population
     *
     * @param person               The object that represents a person at the population
     * @param fitness              The fitness of person
     * @param allowNegativeFitness Allow negative values for person fitness. Default is false
     */
    public Person(T person, double fitness, boolean allowNegativeFitness) {
        this(person, allowNegativeFitness);

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

    public Object getGeneCode() {
        return person;
    }

    public double getFitness() {
        return fitness;
    }

    final void setFitness(double fitness) {
        if (ALLOW_NEGATIVE_FITNESS) {
            this.fitness = fitness;
        }
        else {
            this.fitness = fitness > 0
                ? fitness
                : 0;
        }
    }

    public void setPerson(T person) {
        if (person == null) {
            throw new IllegalArgumentException("Argument, at pos 1: not null.");
        }

        this.person = person;
    }

    @Override
    public int compareTo(Person otherPerson) {
        return Double.compare(fitness, otherPerson.fitness);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return "Person: " + person + "\nFitness: " + fitness + "\nPercent of fitness: " + percentOfFitness;
    }
}

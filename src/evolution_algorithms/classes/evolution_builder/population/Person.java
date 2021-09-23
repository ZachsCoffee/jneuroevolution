/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.population;


/**
 *
 * @author main
 */
public class Person implements Comparable<Person>, Cloneable {
    private final boolean ALLOW_NEGATIVE_FITNESS;
    
    private Object person;
    private double fitness;
    private int percentOfFitness;

    /**
     * Copy a person Object and returns it
     * @param person The person to copy
     * @return The new object of person
     */
    public static Person copyPerson(Person person){
        Person p = new Person(person.person, person.fitness);
        p.percentOfFitness = person.percentOfFitness;
        return p;
    }

    /**
     * Creates a person for the population
     * @param person The object that represents a person at the population
     */
    public Person(Object person) {
        
        if (person == null) throw new IllegalArgumentException(
                "object person not null"
        );
        
        this.person = person;
        
        ALLOW_NEGATIVE_FITNESS = false;
    }
    
    /**
     * Creates a person for the population
     * @param person The object that represents a person at the population
     * @param fitness The fitness of person
     */
    public Person(Object person, double fitness) {
        this(person);
        
        setFitness(fitness);
    }
    
    /**
     * Creates a person for the population
     * @param person The object that represents a person at the population
     * @param allowNegativeFitness Allow negative values for person fitness. Default is false
     */
    public Person(Object person, boolean allowNegativeFitness){
        if (person == null) throw new IllegalArgumentException(
                "object person not null"
        );
        
        this.person = person;
        
        ALLOW_NEGATIVE_FITNESS = allowNegativeFitness;
    }
    
    /**
     * Creates a person for the population
     * @param person The object that represents a person at the population
     * @param fitness The fitness of person
     * @param allowNegativeFitness Allow negative values for person fitness. Default is false
     */
    public Person(Object person, double fitness, boolean allowNegativeFitness){
        this(person, allowNegativeFitness);
        
        setFitness(fitness);
    }

    @Override
    public int compareTo(Person otherPerson) {
        return Double.compare(fitness, otherPerson.fitness);
    }
    
    public int getPercentOfFitness() {
        return percentOfFitness;
    }

    /**
     * Setter for the percentOfFitness
     * @param percentOfFitness The percent range [0-100]
     * @exception IllegalArgumentException If the percent value is negative
     */
    public void setPercentOfFitness(int percentOfFitness){
        if (percentOfFitness < 0) throw new IllegalArgumentException("percent of fitness can't be negative percentOfFitness="+percentOfFitness);
        
        this.percentOfFitness = percentOfFitness;
    }
   
    
    public Object getGeneCode() {
        return person;
    }

    public void setPerson(Object person){
        if (person == null){
            throw new IllegalArgumentException("Argument, at pos 1: not null.");
        }
        
        this.person = person;
    }
    public double getFitness() {
        return fitness;
    }

    final void setFitness(double fitness) {
        if (ALLOW_NEGATIVE_FITNESS){
            this.fitness = fitness;
        }
        else{
            this.fitness = fitness > 0 ? fitness : 0;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public String toString(){
        String print = "";
        double[] table = (double[])person;
        for (int i=0; i<4; i++){
            print += table[i]+" ";
        }
        print += fitness+" "+percentOfFitness;
        return print;
    }
}

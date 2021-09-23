/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.population;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author main
 */
public class Population {

    public synchronized static Population generate(PersonManager personManager) {
        Population tempPopulation = new Population(personManager);

        tempPopulation.createPopulation();

        return tempPopulation;
    }

    public synchronized static Population generate(PersonManager personManager, int size) {
        Population tempPopulation = new Population(personManager, size);

        tempPopulation.createPopulation();

        return tempPopulation;
    }

    private int size = 100;
    private final ArrayList<Person> population = new ArrayList<>();
    
    private final PersonManager personManager;
    private Person bestPerson = null;
    
    public Population(PersonManager personManager){
        if (personManager == null){
            throw new IllegalArgumentException("Argument at pos 1: not null.");
        }
        
        this.personManager = personManager;
    }
    
    public Population(PersonManager personManager, int size){
        this(personManager);
        if (size <= 0){
            throw new IllegalArgumentException("Argument at pos 1: must be greater than zero.");
        }
        
        this.size = size;
    }
    
    //start get/set
    public void addPerson(Person person){
        population.add(person);
    }
    
    public Person getPersonAt(int position){
        return population.get(position);
    }
    public void setPersonAt(Person person, int position){
        population.set(position, person);
    }
    
    public int getSize(){
        return size;
    }
    
    public int getRunningSize(){
        return population.size();
    }
    
    public PersonManager getPersonManager(){
        return personManager;
    }

    public Person getBestPerson() {
        if (bestPerson == null) throw new IllegalStateException(
                "Can't give the best person if the fitness isn't computed"
        );

        return bestPerson;
    }

    //end get/set
    
    //start methods
    public Person createPerson(){
        return personManager.newPerson();
    }
    
    public Person newSameLengthAs(Person person){
        return personManager.newSameLengthAs(person);
    }
    
    public void createPopulation(){//ftiaxnei to population
        for (int i=1; i<=size; i++){
            population.add(personManager.newRandomPerson());
        }
    }
    
    public Person findBestPerson(){
        int bestPos = 0;
        double bestFitness = population.get(0).getFitness();
        for (int i=1; i<size; i++){
            if (population.get(i).getFitness() > bestFitness){
                bestPos = i;
            }
        }
        
        return population.get(bestPos);
    }
    
    public void sortPopulation(){
        Collections.sort(population);
    }

    /**
     * Computes the fitness for all the population persons. At the same time search's for the best population person.
     */
    public void computeFitnessForPopulation() {
        Person populationBestPerson = population.get(0);
        populationBestPerson.setFitness(personManager.computeFitness(populationBestPerson));

        Person tempPerson;
        for (int i=1; i<population.size(); i++) {
            tempPerson = population.get(i);
            tempPerson.setFitness(personManager.computeFitness(tempPerson));

            if (tempPerson.getFitness() > populationBestPerson.getFitness()) {
                populationBestPerson = tempPerson;
            }
        }

        bestPerson = Person.copyPerson(populationBestPerson);
    }
    
    public String toString(){
        StringBuilder print = new StringBuilder();

        for (int i=0; i<size; i++){
            print.append(population.get(i)).append("\n");
        }
        
        return print.toString();
    }
}

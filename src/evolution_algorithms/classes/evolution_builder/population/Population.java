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
public class Population<P> {

    public synchronized static <P> Population<P> generate(PersonManager<P> personManager) {
        Population<P> tempPopulation = new Population<>(personManager);

        tempPopulation.createPopulation();

        return tempPopulation;
    }

    public synchronized static <P> Population<P> generate(PersonManager<P> personManager, int size) {
        Population<P> tempPopulation = new Population<>(personManager, size);

        tempPopulation.createPopulation();

        return tempPopulation;
    }

    private int size = 100;
    private final ArrayList<PopulationPerson<P>> population = new ArrayList<>();
    
    private final PersonManager<P> personManager;
    private PopulationPerson<P> bestPopulationPerson = null;
    
    public Population(PersonManager<P> personManager){
        if (personManager == null){
            throw new IllegalArgumentException("Argument at pos 1: not null.");
        }
        
        this.personManager = personManager;
    }
    
    public Population(PersonManager<P> personManager, int size){
        this(personManager);
        if (size <= 0){
            throw new IllegalArgumentException("Argument at pos 1: must be greater than zero.");
        }
        
        this.size = size;
    }
    
    //start get/set
    public void addPerson(PopulationPerson<P> populationPerson){
        population.add(populationPerson);
    }
    
    public PopulationPerson<P> getPersonAt(int position){
        return population.get(position);
    }
    public void setPersonAt(PopulationPerson<P> populationPerson, int position){
        population.set(position, populationPerson);
    }
    
    public int getSize(){
        return size;
    }
    
    public int getRunningSize(){
        return population.size();
    }
    
    public PersonManager<P> getPersonManager(){
        return personManager;
    }

    public PopulationPerson<P> getBestPerson() {
        if (bestPopulationPerson == null) throw new IllegalStateException(
                "Can't give the best person if the fitness isn't computed"
        );

        return bestPopulationPerson;
    }

    //end get/set
    
    //start methods
    public PopulationPerson<P> createPerson(){
        return personManager.newPerson();
    }
    
    public PopulationPerson<P> newSameLengthAs(PopulationPerson<P> populationPerson){
        return personManager.newSameLengthAs(populationPerson);
    }
    
    public void createPopulation(){//ftiaxnei to population
        for (int i=1; i<=size; i++){
            population.add(personManager.newRandomPerson());
        }
    }
    
    public PopulationPerson<P> findBestPerson(){
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
        PopulationPerson<P> populationBestPopulationPerson = population.get(0);
        populationBestPopulationPerson.setFitness(personManager.computeFitness(populationBestPopulationPerson));

        PopulationPerson<P> tempPopulationPerson;
        for (int i=1; i<population.size(); i++) {
            tempPopulationPerson = population.get(i);
            tempPopulationPerson.setFitness(personManager.computeFitness(tempPopulationPerson));

            if (tempPopulationPerson.getFitness() > populationBestPopulationPerson.getFitness()) {
                populationBestPopulationPerson = tempPopulationPerson;
            }
        }

        bestPopulationPerson = PopulationPerson.copyPerson(populationBestPopulationPerson);
    }
    
    public String toString(){
        StringBuilder print = new StringBuilder();

        for (int i=0; i<size; i++){
            print.append(population.get(i)).append("\n");
        }
        
        return print.toString();
    }
}

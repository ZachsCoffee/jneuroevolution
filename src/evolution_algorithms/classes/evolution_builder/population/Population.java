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
    private int size = 100;
    private final ArrayList<Person> population = new ArrayList<>();
    
    private final PersonManager personManager;
    
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
    void restorePopulation(){//gia na mporw na ksana gemisw to population ama einai fixed size
        int restoreCount = size - population.size();
        for (int i=1; i<=restoreCount; i++){
            population.add(personManager.newPerson());
        }
    }
    //end methods
    
    private void computeFitnessForPopulation(){//returns the best person
        int populationSize = population.size();
        Person tempPerson;
        for (int i=0; i<populationSize; i++){
            tempPerson = population.get(i);
            tempPerson.setFitness(personManager.computeFitness(tempPerson));
        }
    }
    
    public Person computeFitnessForPopulation(Person totalBest){//returns the best person
        //gia best person
        if (totalBest == null){
            totalBest = population.get(0);
            totalBest.setFitness(personManager.computeFitness(totalBest));
        }
        
        int populationSize = population.size();
        Person tempPerson;
        for (int i=0; i<populationSize; i++){
            tempPerson = population.get(i);
            tempPerson.setFitness(personManager.computeFitness(tempPerson));
            
            if (totalBest.getFitness() < tempPerson.getFitness()){
                totalBest = tempPerson;
            }
        }
        return Person.copyPerson(totalBest);
    }
    
    public String toString(){
        String print = "";
        for (int i=0; i<size; i++){
            print += population.get(i)+"\n";
        }
        
        return print;
    }
}

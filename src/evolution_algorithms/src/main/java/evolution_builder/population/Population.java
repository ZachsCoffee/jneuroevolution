/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.population;

import java.util.ArrayList;
import java.util.Collections;

/**
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

    private final ArrayList<PopulationPerson<P>> population;
    private final int populationSize;
    private final PersonManager<P> personManager;
    private PopulationPerson<P> bestPopulationPerson = null;

    public Population(PersonManager<P> personManager) {
        this(personManager, 100);
    }

    public Population(PersonManager<P> personManager, int size) {
        if (personManager == null) {
            throw new IllegalArgumentException("Argument at pos 1: not null.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Argument at pos 1: must be greater than zero.");
        }

        populationSize = size;
        this.personManager = personManager;
        population = new ArrayList<>(populationSize);
    }

    //start get/set
    public void addPerson(PopulationPerson<P> populationPerson) {
        population.add(populationPerson);
    }

    public PopulationPerson<P> getPersonAt(int position) {
        return population.get(position);
    }

    public void setPersonAt(PopulationPerson<P> populationPerson, int position) {
        population.set(position, populationPerson);
    }

    public int getSize() {
        return population.size();
    }

    public int getRunningSize() {
        return population.size();
    }

    public PersonManager<P> getPersonManager() {
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
    public PopulationPerson<P> createPerson() {
        return personManager.newPerson();
    }

    public PopulationPerson<P> newSameLengthAs(PopulationPerson<P> populationPerson) {
        return personManager.newSameLengthAs(populationPerson);
    }

    public void createPopulation() {
        for (int i = 1; i <= populationSize; i++) {
            population.add(personManager.newPerson());
        }
    }

    public PopulationPerson<P> findBestPerson() {
        int bestPos = 0;
        double bestFitness = population.get(0).getFitness();
        int size = population.size();

        for (int i = 1; i < size; i++) {
            if (population.get(i).getFitness() > bestFitness) {
                bestPos = i;
            }
        }

        return population.get(bestPos);
    }

    public void sortPopulation() {
        Collections.sort(population);
    }

    /**
     * Computes the fitness for all the population persons. At the same time search's for the best population person.
     */
    public void computeFitnessForPopulation() {
        PopulationPerson<P> populationBestPopulationPerson = population.get(0);
        populationBestPopulationPerson.setFitness(personManager.computeFitness(populationBestPopulationPerson));

        PopulationPerson<P> tempPopulationPerson;
        for (int i = 1; i < population.size(); i++) {
            tempPopulationPerson = population.get(i);
            tempPopulationPerson.setFitness(personManager.computeFitness(tempPopulationPerson));

            if (tempPopulationPerson.getFitness() > populationBestPopulationPerson.getFitness()) {
                populationBestPopulationPerson = tempPopulationPerson;
            }
        }

        bestPopulationPerson = PopulationPerson.copyPerson(populationBestPopulationPerson);
    }

    public String toString() {
        StringBuilder print = new StringBuilder();

        for (PopulationPerson<P> pPopulationPerson : population) {
            print.append(pPopulationPerson).append("\n");
        }

        return print.toString();
    }
}

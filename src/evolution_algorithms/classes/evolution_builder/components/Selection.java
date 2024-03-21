/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.components;

import evolution_builder.population.Person;
import evolution_builder.population.Population;
import java.util.ArrayList;

/**
 *
 * @author main
 */
public class Selection {
    private Selection(){}
    //megalutero fitness kalutero fitness
    public static Population tournament(Population population, int rankSize, boolean byPercentOfFitness){
        if (rankSize <= 1){
            throw new IllegalArgumentException("Argument, at pos: 1, must be greater than one.");
        }
        
        int size = population.getRunningSize(), randomValue;
        Population newPopulation = new Population(population.getPersonManager(), size);
        
        int personPosition;
        double maxFitness;
        if (byPercentOfFitness){
            for (int i=1; i<=size; i++){
                personPosition = getRandom(size);
                maxFitness = population.getPersonAt(personPosition).getPercentOfFitness();
                for (int j=2; j<=rankSize; j++){
                    randomValue = getRandom(size);
                    if (population.getPersonAt(randomValue).getPercentOfFitness() > maxFitness){
                        maxFitness = population.getPersonAt(randomValue).getPercentOfFitness();
                        personPosition = randomValue;
                    }
                }
                newPopulation.addPerson(population.getPersonAt(personPosition));
            }
        }
        else{
            
            for (int i=1; i<=size; i++){
                personPosition = getRandom(size);
                maxFitness = population.getPersonAt(personPosition).getFitness();
                for (int j=2; j<=rankSize; j++){
                    randomValue = getRandom(size);
                    if (population.getPersonAt(randomValue).getFitness() > maxFitness){
                        maxFitness = population.getPersonAt(randomValue).getFitness();
                        personPosition = randomValue;
                    }
                }
                newPopulation.addPerson(population.getPersonAt(personPosition));
            }
        }
        return newPopulation;
    }
    
    public static Population roulette(Population population, boolean byPercentOfFitness){
        int size = population.getRunningSize();
        Population newPopulation = new Population(population.getPersonManager(), size);
        int addedCount = 0;
        int position;
        
        if (byPercentOfFitness){
            while (addedCount < size){
                position = getRandom(size);
                if (population.getPersonAt(position).getPercentOfFitness() >= getRandom(101)){
                    newPopulation.addPerson(population.getPersonAt(position));
                    addedCount++;
                }
            }
        }
        else{
            while (addedCount < size){
                position = getRandom(size);
                if (population.getPersonAt(position).getFitness()>= getRandom(101)){
                    newPopulation.addPerson(population.getPersonAt(position));
                    addedCount++;
                }
            }
        }
        return newPopulation;
    }
    
    private static int getRandom(int length){
        return (int)(Math.random()*length);
    }
}

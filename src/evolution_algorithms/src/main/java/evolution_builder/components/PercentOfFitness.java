/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.components;

import evolution_builder.population.PopulationPerson;
import evolution_builder.population.Population;

/**
 *
 * @author main
 */
public class PercentOfFitness {

    private PercentOfFitness(){}
    
    public static void percentFromTotalBest(Population population, double bestFitness){
        if (bestFitness < 1)
            throw new IllegalArgumentException("Argument bestFitness, must be greater or equal than 1, current: "+bestFitness);
        
        
        int size = population.getRunningSize();
        PopulationPerson tempPopulationPerson;
        for (int i=0; i<size; i++){
            tempPopulationPerson = population.getPersonAt(i);
            
            if (tempPopulationPerson.getFitness() <= 0){
                tempPopulationPerson.setPercentOfFitness(0);
            }
            else{
                tempPopulationPerson.setPercentOfFitness((int)(100* tempPopulationPerson.getFitness()/bestFitness));
            }
        }
    }
    
    public static void percentFromCurrentBest(Population population){
        int size = population.getRunningSize();
        double currentMax = population.getPersonAt(findMax(population)).getFitness();
        
        if (currentMax < 0) return;
        
        PopulationPerson tempPopulationPerson;
        for (int i=0; i<size; i++){
            tempPopulationPerson = population.getPersonAt(i);
            
            if (tempPopulationPerson.getFitness() <= 0){
                tempPopulationPerson.setPercentOfFitness(0);
            }
            else{
                tempPopulationPerson.setPercentOfFitness((int)percentOf(0, currentMax, tempPopulationPerson.getFitness()));
            }
            
//            tempPerson.setPercentOfFitness((int)(100*tempPerson.getFitness()/currentMax));
        }
    }
    
    public static void percentFromCurrentBestRanked(Population population){
        int size = population.getRunningSize();
        double 
                currentMax = population.getPersonAt(findMax(population)).getFitness(),
                currentMin = population.getPersonAt(findMin(population)).getFitness();
        double max = currentMax - currentMin;
        PopulationPerson tempPopulationPerson;
        for (int i=0; i<size; i++){
            tempPopulationPerson = population.getPersonAt(i);
            tempPopulationPerson.setPercentOfFitness((int)percentOf(currentMin, currentMax, tempPopulationPerson.getFitness()));
//            tempPerson.setPercentOfFitness((int)(100*(tempPerson.getFitness() - currentMin)/max));
        }
    }
    
    private static double percentOf(double min, double max, double current){
        return 100 * (current - min) / (max - min);
    }
    
    private static int findMax(Population population){
        int posMax = 0, size = population.getRunningSize();
        double max = population.getPersonAt(0).getFitness();
        
        for (int i=1; i<size; i++){
            if (population.getPersonAt(i).getFitness() > max){
                max = population.getPersonAt(i).getFitness();
                posMax = i;
            }
        }
        
        return posMax;
    }
    
    private static int findMin(Population population){
        int posMin = 0, size = population.getRunningSize();
        double min = population.getPersonAt(0).getFitness();
        
        for (int i=1; i<size; i++){
            if (population.getPersonAt(i).getFitness() < min){
                min = population.getPersonAt(i).getFitness();
                posMin = i;
            }
        }
        
        return posMin;
    }
}

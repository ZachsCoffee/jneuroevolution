/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.components;

import evolution_builder.population.Genes;
import evolution_builder.population.Person;
import evolution_builder.population.Population;

/**
 *
 * @author main
 */
public class Mutation {
    public static void mutation(Population population, int chance, double maxValue, boolean negative, Genes genes){
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0)) ;
        Person tempPerson;
        
        if (chance < 2){
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than one.");
        }
        
        if (negative){
           for (int i=0; i<populationSize; i++){
                for (int j=0; j<genesCount; j++){
                    if ((int)(Math.random()*chance) == 0){
                        tempPerson = population.getPersonAt(i);

                        genes.mutationValue(tempPerson, j, Math.random()*(maxValue*2)-maxValue);
                    }
                }
            } 
        }
        else{
            for (int i=0; i<populationSize; i++){
                for (int j=0; j<genesCount; j++){
                    if ((int)(Math.random()*chance) == 0){
                        tempPerson = population.getPersonAt(i);

                        genes.mutationValue(tempPerson, j, Math.random()*maxValue);
                    }
                }
            }
        }
    }
    
    public static void variableLength(Population population, int chance, double maxValue, boolean negative, Genes genes){
        int populationSize = population.getSize(), genesCount;
        Person tempPerson;
        
        if (chance < 2){
            throw new IllegalArgumentException("Argument, chance: must be greater than one.");
        }
        
        if (negative){
           for (int i=0; i<populationSize; i++){
                genesCount = genes.genesCount(population.getPersonAt(i));
                for (int j=0; j<genesCount; j++){
                    if ((int)(Math.random()*chance) == 0){
                        tempPerson = population.getPersonAt(i);

                        genes.mutationValue(tempPerson, j, Math.random()*(maxValue*2)-maxValue);
                    }
                }
            } 
        }
        else{
            for (int i=0; i<populationSize; i++){
                genesCount = genes.genesCount(population.getPersonAt(i));
                for (int j=0; j<genesCount; j++){
                    if ((int)(Math.random()*chance) == 0){
                        tempPerson = population.getPersonAt(i);

                        genes.mutationValue(tempPerson, j, Math.random()*maxValue);
                    }
                }
            }
        }
    }
}

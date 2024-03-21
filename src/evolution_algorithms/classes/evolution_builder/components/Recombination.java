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
public class Recombination {
    
    private Recombination(){}
    
    public static <T> Population fixed(Population population, int breakSize, Genes<T> genes){
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;
        
        Person person1, person2, newPerson1, newPerson2;
        
        Population newPopulation = new Population(population.getPersonManager(), populationSize);
        
        if (genesCount <= breakSize){
            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
        }
        else if (breakSize < 1){
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }
        
        boolean changeGenesSide;
        while (personCount < populationSize){
            person1 = population.getPersonAt(getRandom(populationSize));
            person2 = population.getPersonAt(getRandom(populationSize));
            newPerson1 = population.createPerson();
            newPerson2 = population.createPerson();
            
            changeGenesSide = true;
            for (int i=0; i<genesCount; i++){
                if (i%breakSize == 0){
                    changeGenesSide = !changeGenesSide;
                }
                
                if (changeGenesSide){
                    genes.setGenAt(newPerson1, genes.getGenAt(person1, i), i);
                    genes.setGenAt(newPerson2, genes.getGenAt(person2, i), i);
                }
                else{
                    genes.setGenAt(newPerson1, genes.getGenAt(person2, i), i);
                    genes.setGenAt(newPerson2, genes.getGenAt(person1, i), i);
                }
            }
            
            if (personCount + 2 <= populationSize){
                newPopulation.addPerson(newPerson1);
                newPopulation.addPerson(newPerson2);
                personCount += 2;
            }
            else if (personCount + 1 <= populationSize){
                newPopulation.addPerson(newPerson1);
                personCount++;
            }
        }
        
        return newPopulation;
    }
    
    public static <T> Population random(Population population, int breakSize, Genes<T> genes){
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;
        int randomBreakSize;
        
        Person person1, person2, newPerson1, newPerson2;
        
        Population newPopulation = new Population(population.getPersonManager(), populationSize);
        
        if (genesCount <= breakSize){
            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
        }
        else if (breakSize < 1){
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }
        
        boolean changeGenesSide;
        while (personCount < populationSize){
            person1 = population.getPersonAt(getRandom(populationSize));
            person2 = population.getPersonAt(getRandom(populationSize));
            newPerson1 = population.createPerson();
            newPerson2 = population.createPerson();
            
            randomBreakSize = getRandom(breakSize) + 1;
            changeGenesSide = true;
            for (int i=0; i<genesCount; i++){
                if (i%randomBreakSize == 0){
                    changeGenesSide = !changeGenesSide;
                }
                
                if (changeGenesSide){
                    genes.setGenAt(newPerson1, genes.getGenAt(person1, i), i);
                    genes.setGenAt(newPerson2, genes.getGenAt(person2, i), i);
                }
                else{
                    genes.setGenAt(newPerson1, genes.getGenAt(person2, i), i);
                    genes.setGenAt(newPerson2, genes.getGenAt(person1, i), i);
                }
            }
            
            if (personCount + 2 <= populationSize){
                newPopulation.addPerson(newPerson1);
                newPopulation.addPerson(newPerson2);
                personCount += 2;
            }
            else if (personCount + 1 <= populationSize){
                newPopulation.addPerson(newPerson1);
                personCount++;
            }
        }
        
        return newPopulation;
    }
    
    public static <T> Population randomWithFilter(Population population, int breakSize, Genes<T> genes){
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;
        int randomBreakSize;
        
        Person person1 = null, person2 = null, newPerson1, newPerson2;
        Person[] persons;
        Population newPopulation = new Population(population.getPersonManager(), populationSize);
        
        if (genesCount <= breakSize){
            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
        }
        else if (breakSize < 1){
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }
        
        boolean changeGenesSide;
        while (personCount < populationSize){
            //person1 = population.getPersonAt(getRandom(populationSize));
            //person2 = population.getPersonAt(getRandom(populationSize));
            persons = getFilteredPersons(population);
            person1 = persons[0];
            person2 = persons[1];
            
            newPerson1 = population.createPerson();
            newPerson2 = population.createPerson();
            
            randomBreakSize = getRandom(breakSize) + 1;
            changeGenesSide = true;
            for (int i=0; i<genesCount; i++){
                if (i%randomBreakSize == 0){
                    changeGenesSide = !changeGenesSide;
                }
                
                if (changeGenesSide){
                    genes.setGenAt(newPerson1, genes.getGenAt(person1, i), i);
                    genes.setGenAt(newPerson2, genes.getGenAt(person2, i), i);
                }
                else{
                    genes.setGenAt(newPerson1, genes.getGenAt(person2, i), i);
                    genes.setGenAt(newPerson2, genes.getGenAt(person1, i), i);
                }
            }
            
            if (personCount + 2 <= populationSize){
                newPopulation.addPerson(newPerson1);
                newPopulation.addPerson(newPerson2);
                personCount += 2;
            }
            else if (personCount + 1 <= populationSize){
                newPopulation.addPerson(newPerson1);
                personCount++;
            }
        }
        
        return newPopulation;
    }
    
    public static <T> Population fixedWithFilter(Population population, int breakSize, Genes<T> genes){
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;
        
        Person person1 = null, person2 = null, newPerson1, newPerson2;
        Person[] persons;
        Population newPopulation = new Population(population.getPersonManager(), populationSize);
        
        if (genesCount <= breakSize){
            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
        }
        else if (breakSize < 1){
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }
        
        boolean changeGenesSide;
        while (personCount < populationSize){
            //person1 = population.getPersonAt(getRandom(populationSize));
            //person2 = population.getPersonAt(getRandom(populationSize));
            persons = getFilteredPersons(population);
            person1 = persons[0];
            person2 = persons[1];
            
            newPerson1 = population.createPerson();
            newPerson2 = population.createPerson();
            
            changeGenesSide = true;
            for (int i=0; i<genesCount; i++){
                if (i%breakSize == 0){
                    changeGenesSide = !changeGenesSide;
                }
                
                if (changeGenesSide){
                    genes.setGenAt(newPerson1, genes.getGenAt(person1, i), i);
                    genes.setGenAt(newPerson2, genes.getGenAt(person2, i), i);
                }
                else{
                    genes.setGenAt(newPerson1, genes.getGenAt(person2, i), i);
                    genes.setGenAt(newPerson2, genes.getGenAt(person1, i), i);
                }
            }
            
            if (personCount + 2 <= populationSize){
                newPopulation.addPerson(newPerson1);
                newPopulation.addPerson(newPerson2);
                personCount += 2;
            }
            else if (personCount + 1 <= populationSize){
                newPopulation.addPerson(newPerson1);
                personCount++;
            }
        }
        
        return newPopulation;
    }
    
    public static <T> Population variableLength(Population population, int breakSize, Genes<T> genes){
        int populationSize = population.getSize(), person1GenesCount;
        int personCount = 0;
        
        Person person1, person2, newPerson1, newPerson2;
        
        Population newPopulation = new Population(population.getPersonManager(), populationSize);
        
//        if (genesCount <= breakSize){
//            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
//        }
//        else if (breakSize < 1){
//            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
//        }
        if (breakSize < 1){
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }
        
        boolean changeGenesSide;
        int translatedPosition;
        while (personCount < populationSize){
            person1 = population.getPersonAt(getRandom(populationSize));
            person2 = population.getPersonAt(getRandom(populationSize));
            newPerson1 = population.newSameLengthAs(person1);
            newPerson2 = population.newSameLengthAs(person2);
            
            changeGenesSide = true;
            person1GenesCount = genes.genesCount(person1);
            for (int i=0; i<person1GenesCount; i++){
                if (i%breakSize == 0){
                    changeGenesSide = !changeGenesSide;
                }
                
                translatedPosition = getPositionOf(genes, person2, person1, i);
                if (changeGenesSide){
                    genes.setGenAt(newPerson1, genes.getGenAt(person1, i), i);
                    genes.setGenAt(newPerson2, genes.getGenAt(person2, translatedPosition), translatedPosition);
                }
                else{
                    genes.setGenAt(newPerson1, genes.getGenAt(person2, translatedPosition), i);
                    genes.setGenAt(newPerson2, genes.getGenAt(person1, i), translatedPosition);
                }
            }
            
            if (personCount + 2 <= populationSize){
                newPopulation.addPerson(newPerson1);
                newPopulation.addPerson(newPerson2);
                personCount += 2;
            }
            else if (personCount + 1 <= populationSize){
                newPopulation.addPerson(newPerson1);
                personCount++;
            }
        }
        
        return newPopulation;
    }
    
//    public static Population basic(Population population, Genes genes){
//        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
//        int personCount = 0;
//        
//        Person person1, person2, newPerson1, newPerson2;
//        person1 = person2 = null;
//
//        
//        Population newPopulation = new Population(population.getPersonManager(), populationSize);
//        
//        while (personCount < populationSize){
//            newPerson1 = population.createPerson();
//            newPerson2 = population.createPerson();
//            
//            for (int i=0; i<populationSize; i++){
//                person1 = population.getPersonAt(getRandom(populationSize));
//                person2 = population.getPersonAt(getRandom(populationSize));
//
//                recombination(genes, person1, person2, genesCount);
//            }
//            
//            if (personCount + 2 <= populationSize){
//                newPopulation.addPerson(person1);
//                newPopulation.addPerson(person2);
//                personCount += 2;
//            }
//            else if (personCount + 1 <= populationSize){
//                newPopulation.addPerson(person1);
//                personCount++;
//            }
//        }
//        
//        return newPopulation;
//    }
//    
//    private static <T> void recombination(Genes genes, Person parent1, Person parent2, int genesLength){
//        int cutPosition = getRandom(genesLength -2) +1;
//        
//        T swapTemp;
//        for (int i=cutPosition; i<genesLength; i++){
//            swapTemp = (T) genes.getGenAt(parent1, i);
//            
//            genes.setGenAt(
//                    parent1, 
//                    genes.getGenAt(parent2, i), 
//                    i
//            );
//            genes.setGenAt(
//                    parent2, 
//                    swapTemp, 
//                    i
//            );
//        }
//    }
    
    private static <T> int getPositionOf(Genes<T> genes, Person a, Person b, long positionB){
        return (int)(positionB * genes.genesCount(a) / genes.genesCount(b));
//                return (int)(positionB*(genes.genesCount(a) -1) / (genes.genesCount(b) -1));

    }
    
    private static int getRandom(int length){
        return (int)(Math.random()*length);
    }
    
    private static Person[] getFilteredPersons(Population population){
        Person[] persons = new Person[2];
        boolean firstPerson = false;
        int randomPosition, populationSize = population.getSize();
        while (true){
            randomPosition = (int)(Math.random()*populationSize);
            if (!firstPerson){
                if (population.getPersonAt(randomPosition).getPercentOfFitness() >= (int)(Math.random()*100)){
                    persons[0] = population.getPersonAt(randomPosition);
                    firstPerson = true;
                }
                
            }
            else{
                if (population.getPersonAt(randomPosition).getPercentOfFitness() >= (int)(Math.random()*100)){
                    persons[1] = population.getPersonAt(randomPosition);
                    return persons;
                }
            }
        }
    }
}

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
    
    public static <T, P> Population<P> fixed(Population<P> population, int breakSize, Genes<T, P> genes){
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;
        
        Person<P> person1, person2, newPerson1, newPerson2;
        
        Population<P> newPopulation = new Population<>(population.getPersonManager(), populationSize);
        
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
    
    public static <T, P> Population<P> random(Population<P> population, int breakSize, Genes<T, P> genes){
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;
        int randomBreakSize;
        
        Person<P> person1, person2, newPerson1, newPerson2;
        
        Population<P> newPopulation = new Population<>(population.getPersonManager(), populationSize);
        
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
    
    public static <T, P> Population<P> randomWithFilter(Population<P> population, int breakSize, Genes<T, P> genes){
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;
        int randomBreakSize;
        
        Person<P> person1, person2, newPerson1, newPerson2;
        Person<P>[] persons;
        Population<P> newPopulation = new Population<>(population.getPersonManager(), populationSize);
        
        if (genesCount <= breakSize){
            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
        }
        else if (breakSize < 1){
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }
        
        boolean changeGenesSide;
        while (personCount < populationSize){
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
    
    public static <T, P> Population<P> fixedWithFilter(Population<P> population, int breakSize, Genes<T, P> genes){
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;
        
        Person<P> person1, person2, newPerson1, newPerson2;
        Person<P>[] persons;
        Population<P> newPopulation = new Population<>(population.getPersonManager(), populationSize);
        
        if (genesCount <= breakSize){
            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
        }
        else if (breakSize < 1){
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }
        
        boolean changeGenesSide;
        while (personCount < populationSize){
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
    
    public static <T, P> Population<P> variableLength(Population<P> population, int breakSize, Genes<T, P> genes){
        int populationSize = population.getSize(), person1GenesCount;
        int personCount = 0;
        
        Person<P> person1, person2, newPerson1, newPerson2;
        
        Population<P> newPopulation = new Population<>(population.getPersonManager(), populationSize);
        
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
    
    private static <T, P> int getPositionOf(Genes<T, P> genes, Person<P> a, Person<P> b, long positionB){
        return (int)(positionB * genes.genesCount(a) / genes.genesCount(b));
    }
    
    private static int getRandom(int length){
        return (int)(Math.random()*length);
    }
    
    @SuppressWarnings("unchecked")
    private static <P> Person<P>[] getFilteredPersons(Population<P> population){
        Person<P>[] persons = new Person[2];
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

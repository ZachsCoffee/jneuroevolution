/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.components;

import evolution_builder.population.Genes;
import evolution_builder.population.PopulationPerson;
import evolution_builder.population.Population;

/**
 * @author main
 */
public class Recombination {

    private Recombination() {
    }

    public static <T, P> Population<P> fixed(Population<P> population, int breakSize, Genes<T, P> genes) {
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;

        PopulationPerson<P> populationPerson1, populationPerson2;

        Population<P> newPopulation = new Population<>(population.getPersonManager(), populationSize);

        if (genesCount <= breakSize) {
            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
        }
        else if (breakSize < 1) {
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }

        int randomPerson1Position = 0;
        int randomPerson2Position = 1;
        final int SKIP_STEP = breakSize + 1;
        final int BREAK_POINT = breakSize -1;

        while (personCount < populationSize) {
            randomPerson1Position = getRandom(populationSize, randomPerson1Position);
            randomPerson2Position = getRandom(populationSize, randomPerson2Position);

            populationPerson1 = population.getPersonAt(randomPerson1Position);
            populationPerson2 = population.getPersonAt(randomPerson2Position);

            int i = 0;
            while (i < genesCount) {
                T temp = genes.getGenAt(populationPerson1, i);
                genes.setGenAt(populationPerson1, genes.getGenAt(populationPerson2, i), i);
                genes.setGenAt(populationPerson2, temp, i);

                if (i % breakSize == BREAK_POINT) {
                    i += SKIP_STEP;
                }
                else {
                    i++;
                }
            }

            if (personCount + 2 <= populationSize) {
                newPopulation.addPerson(populationPerson1);
                newPopulation.addPerson(populationPerson2);
                personCount += 2;
            }
            else if (personCount + 1 <= populationSize) {
                newPopulation.addPerson(populationPerson1);
                personCount++;
            }
        }

        return newPopulation;
    }

    public static <T, P> Population<P> random(Population<P> population, int breakSize, Genes<T, P> genes) {
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;
        int randomBreakSize;

        PopulationPerson<P> populationPerson1, populationPerson2, newPopulationPerson1, newPopulationPerson2;

        Population<P> newPopulation = new Population<>(population.getPersonManager(), populationSize);

        if (genesCount <= breakSize) {
            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
        }
        else if (breakSize < 1) {
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }

        boolean changeGenesSide;
        while (personCount < populationSize) {
            populationPerson1 = population.getPersonAt(getRandom(populationSize));
            populationPerson2 = population.getPersonAt(getRandom(populationSize));
            newPopulationPerson1 = population.createPerson();
            newPopulationPerson2 = population.createPerson();

            randomBreakSize = getRandom(breakSize) + 1;
            changeGenesSide = true;
            for (int i = 0; i < genesCount; i++) {
                if (i % randomBreakSize == 0) {
                    changeGenesSide = ! changeGenesSide;
                }

                if (changeGenesSide) {
                    genes.setGenAt(newPopulationPerson1, genes.getGenAt(populationPerson1, i), i);
                    genes.setGenAt(newPopulationPerson2, genes.getGenAt(populationPerson2, i), i);
                }
                else {
                    genes.setGenAt(newPopulationPerson1, genes.getGenAt(populationPerson2, i), i);
                    genes.setGenAt(newPopulationPerson2, genes.getGenAt(populationPerson1, i), i);
                }
            }

            if (personCount + 2 <= populationSize) {
                newPopulation.addPerson(newPopulationPerson1);
                newPopulation.addPerson(newPopulationPerson2);
                personCount += 2;
            }
            else if (personCount + 1 <= populationSize) {
                newPopulation.addPerson(newPopulationPerson1);
                personCount++;
            }
        }

        return newPopulation;
    }

    public static <T, P> Population<P> randomWithFilter(Population<P> population, int breakSize, Genes<T, P> genes) {
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;
        int randomBreakSize;

        PopulationPerson<P> populationPerson1, populationPerson2, newPopulationPerson1, newPopulationPerson2;
        PopulationPerson<P>[] populationPeople;
        Population<P> newPopulation = new Population<>(population.getPersonManager(), populationSize);

        if (genesCount <= breakSize) {
            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
        }
        else if (breakSize < 1) {
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }

        boolean changeGenesSide;
        while (personCount < populationSize) {
            populationPeople = getFilteredPersons(population);
            populationPerson1 = populationPeople[0];
            populationPerson2 = populationPeople[1];

            newPopulationPerson1 = population.createPerson();
            newPopulationPerson2 = population.createPerson();

            randomBreakSize = getRandom(breakSize) + 1;
            changeGenesSide = true;
            for (int i = 0; i < genesCount; i++) {
                if (i % randomBreakSize == 0) {
                    changeGenesSide = ! changeGenesSide;
                }

                if (changeGenesSide) {
                    genes.setGenAt(newPopulationPerson1, genes.getGenAt(populationPerson1, i), i);
                    genes.setGenAt(newPopulationPerson2, genes.getGenAt(populationPerson2, i), i);
                }
                else {
                    genes.setGenAt(newPopulationPerson1, genes.getGenAt(populationPerson2, i), i);
                    genes.setGenAt(newPopulationPerson2, genes.getGenAt(populationPerson1, i), i);
                }
            }

            if (personCount + 2 <= populationSize) {
                newPopulation.addPerson(newPopulationPerson1);
                newPopulation.addPerson(newPopulationPerson2);
                personCount += 2;
            }
            else if (personCount + 1 <= populationSize) {
                newPopulation.addPerson(newPopulationPerson1);
                personCount++;
            }
        }

        return newPopulation;
    }

    public static <T, P> Population<P> fixedWithFilter(Population<P> population, int breakSize, Genes<T, P> genes) {
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        int personCount = 0;

        PopulationPerson<P> populationPerson1, populationPerson2, newPopulationPerson1, newPopulationPerson2;
        PopulationPerson<P>[] populationPeople;
        Population<P> newPopulation = new Population<>(population.getPersonManager(), populationSize);

        if (genesCount <= breakSize) {
            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
        }
        else if (breakSize < 1) {
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }

        boolean changeGenesSide;
        while (personCount < populationSize) {
            populationPeople = getFilteredPersons(population);
            populationPerson1 = populationPeople[0];
            populationPerson2 = populationPeople[1];

            newPopulationPerson1 = population.createPerson();
            newPopulationPerson2 = population.createPerson();

            changeGenesSide = true;
            for (int i = 0; i < genesCount; i++) {
                if (i % breakSize == 0) {
                    changeGenesSide = ! changeGenesSide;
                }

                if (changeGenesSide) {
                    genes.setGenAt(newPopulationPerson1, genes.getGenAt(populationPerson1, i), i);
                    genes.setGenAt(newPopulationPerson2, genes.getGenAt(populationPerson2, i), i);
                }
                else {
                    genes.setGenAt(newPopulationPerson1, genes.getGenAt(populationPerson2, i), i);
                    genes.setGenAt(newPopulationPerson2, genes.getGenAt(populationPerson1, i), i);
                }
            }

            if (personCount + 2 <= populationSize) {
                newPopulation.addPerson(newPopulationPerson1);
                newPopulation.addPerson(newPopulationPerson2);
                personCount += 2;
            }
            else if (personCount + 1 <= populationSize) {
                newPopulation.addPerson(newPopulationPerson1);
                personCount++;
            }
        }

        return newPopulation;
    }

    public static <T, P> Population<P> variableLength(Population<P> population, int breakSize, Genes<T, P> genes) {
        int populationSize = population.getSize(), person1GenesCount;
        int personCount = 0;

        PopulationPerson<P> populationPerson1, populationPerson2, newPopulationPerson1, newPopulationPerson2;

        Population<P> newPopulation = new Population<>(population.getPersonManager(), populationSize);

//        if (genesCount <= breakSize){
//            throw new IllegalArgumentException("Argument, at pos 2: must be less than genes count.");
//        }
//        else if (breakSize < 1){
//            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
//        }
        if (breakSize < 1) {
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than zero.");
        }

        boolean changeGenesSide;
        int translatedPosition;
        while (personCount < populationSize) {
            populationPerson1 = population.getPersonAt(getRandom(populationSize));
            populationPerson2 = population.getPersonAt(getRandom(populationSize));
            newPopulationPerson1 = population.newSameLengthAs(populationPerson1);
            newPopulationPerson2 = population.newSameLengthAs(populationPerson2);

            changeGenesSide = true;
            person1GenesCount = genes.genesCount(populationPerson1);
            for (int i = 0; i < person1GenesCount; i++) {
                if (i % breakSize == 0) {
                    changeGenesSide = ! changeGenesSide;
                }

                translatedPosition = getPositionOf(genes, populationPerson2, populationPerson1, i);
                if (changeGenesSide) {
                    genes.setGenAt(newPopulationPerson1, genes.getGenAt(populationPerson1, i), i);
                    genes.setGenAt(
                        newPopulationPerson2,
                        genes.getGenAt(populationPerson2, translatedPosition),
                        translatedPosition
                    );
                }
                else {
                    genes.setGenAt(newPopulationPerson1, genes.getGenAt(populationPerson2, translatedPosition), i);
                    genes.setGenAt(newPopulationPerson2, genes.getGenAt(populationPerson1, i), translatedPosition);
                }
            }

            if (personCount + 2 <= populationSize) {
                newPopulation.addPerson(newPopulationPerson1);
                newPopulation.addPerson(newPopulationPerson2);
                personCount += 2;
            }
            else if (personCount + 1 <= populationSize) {
                newPopulation.addPerson(newPopulationPerson1);
                personCount++;
            }
        }

        return newPopulation;
    }

    private static <T, P> int getPositionOf(
        Genes<T, P> genes,
        PopulationPerson<P> a,
        PopulationPerson<P> b,
        long positionB
    ) {
        return (int) (positionB * genes.genesCount(a) / genes.genesCount(b));
    }

    private static int getRandom(int length) {
        return (int) (Math.random() * length);
    }

    private static int getRandom(int length, int oldRandomPosition) {
        int newRandomPosition;

        do {
            newRandomPosition = (int) (Math.random() * length);
        } while (newRandomPosition != oldRandomPosition);

        return newRandomPosition;
    }

    @SuppressWarnings("unchecked")
    private static <P> PopulationPerson<P>[] getFilteredPersons(Population<P> population) {
        PopulationPerson<P>[] populationPeople = new PopulationPerson[2];
        boolean firstPerson = false;
        int randomPosition, populationSize = population.getSize();
        while (true) {
            randomPosition = (int) (Math.random() * populationSize);
            if (! firstPerson) {
                if (population.getPersonAt(randomPosition).getPercentOfFitness() >= (int) (Math.random() * 100)) {
                    populationPeople[0] = population.getPersonAt(randomPosition);
                    firstPerson = true;
                }

            }
            else {
                if (population.getPersonAt(randomPosition).getPercentOfFitness() >= (int) (Math.random() * 100)) {
                    populationPeople[1] = population.getPersonAt(randomPosition);
                    return populationPeople;
                }
            }
        }
    }
}

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
public class Mutation {

    public static <P, T> void mutation(
        Population<P> population,
        int chance,
        double maxValue,
        boolean negative,
        Genes<T, P> genes
    ) {
        int populationSize = population.getSize(), genesCount = genes.genesCount(population.getPersonAt(0));
        PopulationPerson<P> tempPopulationPerson;

        if (chance < 2) {
            throw new IllegalArgumentException("Argument, at pos 2: must be greater than one.");
        }

        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < genesCount; j++) {
                if ((int) (Math.random() * chance) == 0) {
                    tempPopulationPerson = population.getPersonAt(i);

                    genes.mutationValue(
                        tempPopulationPerson,
                        j,
                        generateRandomValue(maxValue, negative),
                        maxValue,
                        negative
                    );
                }
            }
        }
    }

    private static double generateRandomValue(double maxValue, boolean negative) {
        return negative
            ? Math.random() * (maxValue * 2) - maxValue
            : Math.random() * maxValue;
    }

    public static <P, T> void variableLength(
        Population<P> population,
        int chance,
        double maxValue,
        boolean negative,
        Genes<T, P> genes
    ) {
        int populationSize = population.getSize(), genesCount;
        PopulationPerson<P> tempPopulationPerson;

        if (chance < 2) {
            throw new IllegalArgumentException("Argument, chance: must be greater than one.");
        }

        for (int i = 0; i < populationSize; i++) {
            genesCount = genes.genesCount(population.getPersonAt(i));
            for (int j = 0; j < genesCount; j++) {
                if ((int) (Math.random() * chance) == 0) {
                    tempPopulationPerson = population.getPersonAt(i);

                    genes.mutationValue(
                        tempPopulationPerson,
                        j,
                        generateRandomValue(maxValue, negative),
                        maxValue,
                        negative
                    );
                }
            }
        }
    }
}

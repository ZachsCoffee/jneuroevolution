package components;

import evolution_builder.components.Recombination;
import evolution_builder.population.Genes;
import evolution_builder.population.PersonManager;
import evolution_builder.population.Population;
import evolution_builder.population.PopulationPerson;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RecombinationTest {

    @ParameterizedTest
    @MethodSource("testFixed")
    public void testFixed(
        int[] person1,
        int[] person2,
        int breakSize,
        int[][] expected
    ) {
        PersonManager<int[]> personManagerMock = mock(PersonManager.class);

        PopulationPerson<int[]> populationPerson1 = new PopulationPerson<>(person1);
        PopulationPerson<int[]> populationPerson2 = new PopulationPerson<>(person2);

        Population<int[]> populationSpy = spy(new Population<>(personManagerMock));

        doReturn(populationPerson1, populationPerson1, populationPerson2)
            .when(populationSpy)
            .getPersonAt(anyInt());

        doReturn(2)
            .when(populationSpy)
            .getSize();

        doReturn(personManagerMock)
            .when(populationSpy)
            .getPersonManager();

        populationSpy = Recombination.fixed(populationSpy, breakSize, new GenesStub(person1.length));

        int[] recombinedPerson1 = populationSpy
            .getPersonAt(0)
            .getGeneCode();

        int[] recombinedPerson2 = populationSpy
            .getPersonAt(1)
            .getGeneCode();

        assertEquals(recombinedPerson1.length, recombinedPerson2.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[0][i], recombinedPerson1[i], "Failed on index: " + i);
            assertEquals(expected[1][i], recombinedPerson2[i], "Failed on index: " + i);
        }
    }

    private static Stream<Arguments> testFixed() {
        return Stream.of(
            arguments(
                new int[]{
                    1, 1, 1, 2, 2, 2, 2, 3, 3
                },
                new int[]{
                    - 1, - 1, - 1, - 1, - 1, - 1, - 1, - 1, - 1,
                },
                2,
                new int[][]{
                    {- 1, - 1, 1, 2, - 1, - 1, 2, 3, - 1},
                    {1, 1, - 1, - 1, 2, 2, - 1, - 1, 3},
                }
            ),
            arguments(
                new int[]{
                    1, 1, 1, 2, 2, 9
                },
                new int[]{
                    - 1, - 1, - 1, - 1, - 1, - 1
                },
                3,
                new int[][]{
                    {- 1, - 1, -1, 2, 2, 9},
                    {1, 1, 1, - 1, -1, -1},
                }
            ),
            arguments(
                new int[]{
                    1, 1, 1, 2, 2, 9
                },
                new int[]{
                    - 1, - 1, - 1, - 1, - 1, - 1
                },
                1,
                new int[][]{
                    {- 1, 1, -1, 2, -1, 9},
                    {1, -1, 1, - 1, 2, -1},
                }
            )
        );
    }

    private PopulationPerson<int[]> createPerson() {
        return new PopulationPerson(new int[9]);
    }

    private static class GenesStub implements Genes<Integer, int[]> {

        private final int genesCount;

        public GenesStub(int genesCount) {
            this.genesCount = genesCount;
        }

        @Override
        public Integer getGenAt(PopulationPerson<int[]> populationPerson, int position) {
            return populationPerson.getGeneCode()[position];
        }

        @Override
        public void setGenAt(PopulationPerson<int[]> populationPerson, Integer gene, int position) {
            populationPerson.getGeneCode()[position] = gene;
        }

        @Override
        public void mutationValue(PopulationPerson<int[]> populationPerson, int position, double mutationValue) {

        }

        @Override
        public int genesCount(PopulationPerson<int[]> populationPerson) {
            return genesCount;
        }
    }
}

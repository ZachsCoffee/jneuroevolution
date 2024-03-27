package rover;

import com.landscraper.rover.common.Direction;
import com.landscraper.rover.simulation.Simulation;
import com.landscraper.rover.simulation.entities.Map;
import data_manipulation.RawDataset;
import evolution_builder.components.Mutation;
import evolution_builder.components.Recombination;
import evolution_builder.components.Selection;
import evolution_builder.population.Population;
import execution.common.GenericProblem;
import execution.common.NeuroevolutionPersonManager;
import functions.ActivationFunction;
import networks.interfaces.Network;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;
import neuroevolution.NeuroevolutionGenes;
import com.landscraper.rover.simulation.entities.rover.RoverAction;
import com.landscraper.rover.common.BlockType;

public class RoverProblem implements GenericProblem<Network> {
    public static final int
            POSSIBLE_BLOCK_TYPES = BlockType.values().length,
            VISION_RANGE = 11 * 11,
            ROVER_POSITION = 2,
            STORAGE_LEVEL = 1,
            ENERGY_LEVEL = 1,
            BASE_POSITION = 2,
            DIRECTIONS = Direction.values().length,
            ROVER_ACTIONS_COUNT = RoverAction.values().length;
    public static final int INPUT_LENGTH = POSSIBLE_BLOCK_TYPES * VISION_RANGE + ROVER_POSITION + STORAGE_LEVEL + ENERGY_LEVEL + BASE_POSITION;

    private final NeuroevolutionGenes<Network> genes = new NeuroevolutionGenes<>();
    private final GenericNeuroevolutionManager<Network> personManager = new GenericNeuroevolutionManager<>(this);

    public RoverProblem() {

    }

    @Override
    public Network buildNetwork(int maxStartValue) {
        return buildNetwork();
    }

    @Override
    public Network buildRandomNetwork(int maxStartValue) {
        return buildNetwork();
    }

    private Network buildNetwork() {
        return NeuralNetworkBuilder.initialize(INPUT_LENGTH)
//                .addLayer(100, ActivationFunction.SIGMOID.getFunction())
//                .addLayer(100, ActivationFunction.SIGMOID.getFunction())
//                .addLayer(50, ActivationFunction.SIGMOID.getFunction())
                .addLayer(ROVER_ACTIONS_COUNT + DIRECTIONS, ActivationFunction.SIGMOID.getFunction())
                .build();

    }

    @Override
    public double evaluateNetwork(Network network, RawDataset rawDataset) {
        Simulation simulation = new Simulation(new Map(100, 100), true);
        RoverInteraction roverInteractions = new RoverInteraction(
                network,
                simulation.getWorld(),
                simulation::terminateSimulation);

        simulation.start(roverInteractions);

        return roverInteractions.getPoints();
//        return Simulation.TOTAL_ROCKS * RoverInteraction.ROCK_POINTS +
//                Simulation.TOTAL_BIOCHEMICAL_REACTIONS * RoverInteraction.BIOCHEMICAL_REACTIONS_POINTS -
//                roverInteractions.getPoints();
    }

    @Override
    public void computePercentOfFitness(Population<Network> population) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Population<Network> recombinationOperator(Population<Network> population, int epoch) {
        return Recombination.random(population, 3, genes);
    }

    @Override
    public Population<Network> selectionMethod(Population<Network> population) {
        return Selection.tournament(population, 2, false);
    }

    @Override
    public void mutationMethod(Population<Network> population, int epoch, int maxEpoch) {
        Mutation.mutation(population, 100, 2, true, genes);
    }

    @Override
    public NeuroevolutionPersonManager<Network> getPersonManager() {
        return personManager;
    }
}

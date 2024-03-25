package rover;

import com.landscraper.rover.common.BlockType;
import com.landscraper.rover.common.Direction;
import com.landscraper.rover.common.Position;
import com.landscraper.rover.simulation.AbstractRoverInteraction;
import com.landscraper.rover.simulation.entities.rover.Rover;
import com.landscraper.rover.simulation.entities.rover.RoverAction;
import com.landscraper.rover.world.World;
import networks.interfaces.Network;

import java.util.List;

public class RoverInteraction extends AbstractRoverInteraction {

    public static final int ROCK_POINTS = 100, BIOCHEMICAL_REACTIONS_POINTS = 300;
    private final Network network;
    private final Runnable diedAction;
    private int points = 0;

    public RoverInteraction(Network network, World world, Runnable diedAction) {
        super(world);
        this.network = network;
        this.diedAction = diedAction;
    }

    @Override
    public void died() {
        diedAction.run();
    }

    @Override
    public void storageUnloaded(int amount) {
        points += ROCK_POINTS * amount;
    }

    @Override
    public void biochemicalReactionObserved(Position position) {
        super.biochemicalReactionObserved(position);

        points += BIOCHEMICAL_REACTIONS_POINTS;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public RoverAction getNextAction(Rover rover) {
        double[] input = normalizeRover(rover);

        double[] output = network.compute(input);

        RoverAction nextRoverAction = softmaxAndGetAction(0, RoverProblem.ROVER_ACTIONS_COUNT, output);

        if (nextRoverAction == RoverAction.MOVE) {
            rover.setDirection(
                    softmaxAndGetDirection(RoverProblem.ROVER_ACTIONS_COUNT, output.length, output)
            );
        }

        return nextRoverAction;
    }

    private double[] normalizeRover(Rover rover) {
        double[] input = new double[RoverProblem.INPUT_LENGTH];
        List<BlockType>[][] vision = rover.queryVision();

        int inputIndex = 0;

        for (int i = 0; i < vision.length; i++) {
            for (int j = 0; j < vision[i].length; j++) {
                for (BlockType blockType : BlockType.values()) {
                    var blockTypes = vision[i][j];
                    if (blockTypes == null) {
                        continue;
                    }
                    input[inputIndex] = blockTypes.contains(blockType)
                        ? 1
                        : 0;
                    inputIndex++;
                }
            }
        }

        input[inputIndex++] = rover.getPosition().x;
        input[inputIndex++] = rover.getPosition().y;
        input[inputIndex++] = rover.getStorageLevel();
        input[inputIndex++] = rover.getEnergyLevel();

        input[inputIndex++] = rover.getEnergyLevel();
        Position basePosition = world.getBase().getPosition();

        input[inputIndex++] = basePosition.x;
        input[inputIndex++] = basePosition.y;

        return input;
    }

    private RoverAction softmaxAndGetAction(int from, int to, double[] results) {
        int position = softmax(from, to, results);

        return RoverAction.values()[position - from];
    }

    private Direction softmaxAndGetDirection(int from, int to, double[] results) {
        int position = softmax(from, to, results);

        return Direction.values()[position - from];
    }

    private int softmax(int from, int to, double[] results) {
        double divider = 0;
        for (int i = from; i< to; i++) {
            divider += Math.exp(results[i]);
        }

        double max = 0;
        int position = from;
        for (int i = from; i< to; i++) {
            double result =  Math.exp(results[i]) / divider;
            if (result > max) {
               max = result;
               position = i;
            }
        }
        return position;
    }
}

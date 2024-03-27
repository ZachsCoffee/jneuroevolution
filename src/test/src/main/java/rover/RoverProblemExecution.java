package rover;

import execution.GenericProblemExecutor;
import networks.interfaces.Network;

public class RoverProblemExecution extends GenericProblemExecutor<Network, RoverProblem> {

    public RoverProblemExecution(RoverProblem problem) {
        super(problem);

        threads = 4;
        epochs = 100;
        populationSize = 5;
    }

    @Override
    protected void evaluation(Network bestPerson) {

    }

    public static void main(String[] args) {
        RoverProblemExecution roverProblemExecution = new RoverProblemExecution(new RoverProblem());
        roverProblemExecution.execute();
    }
}

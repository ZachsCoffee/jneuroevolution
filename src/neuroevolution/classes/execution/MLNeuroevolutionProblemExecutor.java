package execution;

import abstraction.MLProblem;
import execution.common.DataBinder;

public abstract class MLNeuroevolutionProblemExecutor extends NeuroevolutionProblemExecutor implements MLProblem {

    public MLNeuroevolutionProblemExecutor(NeuroevolutionPersonManager personManager, DataBinder dataBinder) {
        super(personManager, dataBinder);
    }
}

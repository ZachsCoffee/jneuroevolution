package execution;

import data_manipulation.RawDataset;
import execution.common.DataBinder;

import networks.interfaces.Network;

public abstract class NeuroevolutionProblemExecutor<P> extends ProblemExecutor<P, RawDataset> {

    public NeuroevolutionProblemExecutor(DataBinder dataBinder, Problem<P, RawDataset> problem) {
        super(dataBinder, problem);
    }

    public abstract EvaluationResult evaluation(Network network, RawDataset rawDataset);


}

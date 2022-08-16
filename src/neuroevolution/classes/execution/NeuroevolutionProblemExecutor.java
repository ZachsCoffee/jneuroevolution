package execution;

import core.layer.TrainableLayer;
import data_manipulation.DatasetTarget;
import data_manipulation.RawDataset;
import execution.common.DataBinder;

import networks.interfaces.Network;

public abstract class NeuroevolutionProblemExecutor<P extends TrainableLayer, D extends DatasetTarget, T extends Problem<P, D>> extends ProblemExecutor<P, D, T> {

    public NeuroevolutionProblemExecutor(DataBinder dataBinder, T problem) {
        super(dataBinder, problem);
    }

    public abstract EvaluationResult evaluation(Network network, RawDataset rawDataset);
}

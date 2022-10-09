package execution;

import core.layer.TrainableLayer;
import data_manipulation.DatasetTarget;
import execution.common.NeuroevolutionProblem;
import networks.interfaces.Network;

public abstract class AbstractNeuroevolutionProblem<P extends TrainableLayer, D extends DatasetTarget> extends Problem<P, D > implements NeuroevolutionProblem<Network> {

}

package execution;

import networks.interfaces.Network;

public class ExecutionResponse {
    final Network network;
    final NeuroevolutionProblemExecutor.EvaluationResult evaluationResult;


    public ExecutionResponse(Network network, NeuroevolutionProblemExecutor.EvaluationResult evaluationResult) {
        this.network = network;
        this.evaluationResult = evaluationResult;
    }

    public Network getNetwork() {
        return network;
    }

    public NeuroevolutionProblemExecutor.EvaluationResult getEvaluationResult() {
        return evaluationResult;
    }
}

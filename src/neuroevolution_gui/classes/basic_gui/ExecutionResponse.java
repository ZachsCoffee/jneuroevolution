package basic_gui;

import networks.interfaces.Network;

public class ExecutionResponse {
    final Network network;
    final ProblemExecutor.EvaluationResult evaluationResult;


    public ExecutionResponse(Network network, ProblemExecutor.EvaluationResult evaluationResult) {
        this.network = network;
        this.evaluationResult = evaluationResult;
    }

    public Network getNetwork() {
        return network;
    }

    public ProblemExecutor.EvaluationResult getEvaluationResult() {
        return evaluationResult;
    }
}

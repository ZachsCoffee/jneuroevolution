package execution;

import core.layer.TrainableLayer;

public class ExecutionResponse {
    final TrainableLayer network;
    final NeuroevolutionProblemExecutor.EvaluationResult evaluationResult;


    public ExecutionResponse(TrainableLayer network, NeuroevolutionProblemExecutor.EvaluationResult evaluationResult) {
        this.network = network;
        this.evaluationResult = evaluationResult;
    }

    public TrainableLayer getNetwork() {
        return network;
    }

    public NeuroevolutionProblemExecutor.EvaluationResult getEvaluationResult() {
        return evaluationResult;
    }
}

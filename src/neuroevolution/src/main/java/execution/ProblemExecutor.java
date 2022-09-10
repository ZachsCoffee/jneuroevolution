package execution;

import core.layer.TrainableLayer;
import data_manipulation.DatasetTarget;
import evolution_builder.Evolution;
import evolution_builder.population.PersonMigration;
import evolution_builder.population.Population;
import execution.common.DataBinder;
import maths.Maths;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ProblemExecutor<P extends TrainableLayer, D extends DatasetTarget, T extends Problem<P, D>> implements Stage.ProgressListener {

    private final DataBinder dataBinder;
    private final AtomicInteger progressCounter = new AtomicInteger();
    private final T problem;
    protected EvaluationTarget evaluationTarget = EvaluationTarget.EVOLUTION_BEST;
    protected boolean
        percentOfFitness = false,
        personMigration = true;
    protected double
        migrationPercent = 0.1;
    protected int
        threads = 5,
        epochs = 2000,
        populationSize = 40;
    private HashMap<Long, Integer> threadToDatasetMap = null;

    private int totalEpochs;

    public ProblemExecutor(
        DataBinder dataBinder,
        T problem
    ) {
        this.problem = Objects.requireNonNull(problem);
        if (dataBinder == null) throw new IllegalArgumentException(
            "Argument dataBinder not null"
        );

        this.dataBinder = dataBinder;
    }

    public abstract void executionEnds(ExecutionResponse[] responses);

    protected abstract EvaluationResult evaluation(P person, D dataset);

    public T getProblem() {
        return problem;
    }

    public void execute() {
        progressCounter.set(0);
        // compute the total epochs
        totalEpochs = threads * epochs;

        threadToDatasetMap = new HashMap<>();

        PersonMigration personMigration = new PersonMigration(migrationPercent, epochs, threads);

        final ExecutionResponse[] executionResponses = new ExecutionResponse[threads];

        final AtomicInteger threadCount = new AtomicInteger(0);
        final long startTimestamp = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {


            final int internalI = i;

            new Thread(() -> {

                long threadId = Thread.currentThread().getId();

                threadToDatasetMap.put(threadId, internalI);

                Evolution<P> evolution = new Evolution<>(
                    Population.generate(problem.getPersonManager(), populationSize),
                    percentOfFitness,
                    problem
                );

                if (this.personMigration) personMigration.addPopulation(evolution);

                Stage<P, D> stage = new Stage<>(problem, epochs);

                stage.setProgressListener(this);

                if (this.personMigration) stage.setMigration(personMigration);

                evolution.startEvolution(epochs, stage);

                P trainableLayer = null;
                switch (evaluationTarget) {
                    case EVOLUTION_BEST:

                        trainableLayer = evolution.getTotalBestPerson().getGeneCode();
                        break;
                    case VALIDATION_BEST:

                        trainableLayer = stage.getValidationBestPerson().getGeneCode();
                        break;
                }

                EvaluationResult testResult = evaluation(trainableLayer, problem.getTestingDataset());

                dataBinder.addResults(new ResultsData(
                    stage.getEvolutionStatistics(),
                    stage.getValidationStatistics(),
                    problem.getTestingDataset().getTargets(),
                    testResult.predictionValues,
                    String.format(
                        "[ Evaluation: %.7f ] [ Thread ID: %d ] [ Validation: %.7f ] [ Training: %.7f ]",
                        testResult.evaluationError,
                        threadId,
                        stage.getValidationBestFitness(),
                        evolution.getTotalBestPerson().getFitness()
                    )
                ));

                executionResponses[threadCount.getAndIncrement()] = new ExecutionResponse(trainableLayer, testResult);

                if (threadCount.get() == threads) {
                    executionEnds(executionResponses);
                    System.out.printf(
                        "Total execution time: %.2fs",
                        (System.currentTimeMillis() - startTimestamp) / 1000d
                    );
                }
            }).start();
        }
    }

    @Override
    public void epochUpdate(int currentEpoch) {
        dataBinder.addProgress((int) Maths.percent(totalEpochs, progressCounter.incrementAndGet()));
    }

    @Override
    public final synchronized void evolutionBestUpdate(double bestPersonFitness) {

    }

    protected ExecutionResponse findBest(ExecutionResponse[] executionResponses) {
        ExecutionResponse bestExecutionResponse = executionResponses[0];

        for (int i = 1; i < executionResponses.length; i++) {
            if (executionResponses[i].evaluationResult.evaluationError < bestExecutionResponse.evaluationResult.evaluationError) {
                bestExecutionResponse = executionResponses[i];
            }
        }

        return bestExecutionResponse;
    }

    public static class EvaluationResult {

        public final double[][] predictionValues;
        public final double evaluationError;

        public EvaluationResult(double[][] predictionValues, double evaluationError) {
            this.predictionValues = predictionValues;
            this.evaluationError = evaluationError;
        }
    }
}

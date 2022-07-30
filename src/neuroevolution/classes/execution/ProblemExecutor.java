package execution;

import data_manipulation.DatasetTarget;
import evolution_builder.Evolution;
import evolution_builder.population.PersonMigration;
import evolution_builder.population.Population;
import execution.common.DataBinder;
import maths.Maths;
import networks.interfaces.Network;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ProblemExecutor<P, D extends DatasetTarget> implements Stage.ProgressListener {

    private final DataBinder dataBinder;
    private final AtomicInteger progressCounter = new AtomicInteger();
    private final Problem<P, D> problem;
    protected EvaluationTarget EVALUATION_TARGET;
    protected boolean
        PERCENT_OF_FITNESS = false,
        PERSON_MIGRATION = true;
    protected double
        MIGRATION_PERCENT = 0.1;
    protected int
        THREADS = 5,
        EPOCHS = 2000,
        POPULATION_SIZE = 40;
    private HashMap<Long, Integer> threadToDatasetMap = null;
    private double[] evolutionBest; // holds the (best person fitness) for each subpopulation

    private int totalEpochs;

    public ProblemExecutor(
        DataBinder dataBinder,
        Problem<P, D> problem
    ) {
        this.problem = Objects.requireNonNull(problem);
        if (dataBinder == null) throw new IllegalArgumentException(
            "Argument dataBinder not null"
        );

        this.dataBinder = dataBinder;
    }

    public abstract void executionEnds(ExecutionResponse[] responses);

    protected abstract EvaluationResult evaluation(D dataset);

    public void execute() {
        progressCounter.set(0);
        // compute the total epochs
        totalEpochs = THREADS * EPOCHS;

        evolutionBest = new double[THREADS];

        threadToDatasetMap = new HashMap<>();

        PersonMigration personMigration = new PersonMigration(MIGRATION_PERCENT, EPOCHS, THREADS);

        final ExecutionResponse[] executionResponses = new ExecutionResponse[THREADS];

        final AtomicInteger threadCount = new AtomicInteger(0);
        final long startTimestamp = System.currentTimeMillis();
        for (int i = 0; i < THREADS; i++) {


            final int internalI = i;

            new Thread(() -> {

                long threadId = Thread.currentThread().getId();

                threadToDatasetMap.put(threadId, internalI);

                Evolution evolution = new Evolution(
                    Population.generate(problem.getPersonManager(), POPULATION_SIZE),
                    PERCENT_OF_FITNESS,
                    problem
                );

                if (PERSON_MIGRATION) personMigration.addPopulation(evolution);

                Stage<P, D> stage = new Stage<>(problem, EPOCHS);

                stage.setProgressListener(this);

                if (PERSON_MIGRATION) stage.setMigration(personMigration);

                evolution.startEvolution(EPOCHS, stage);

                Network network = null;
                switch (EVALUATION_TARGET) {
                    case EVOLUTION_BEST:

                        network = (Network) evolution.getTotalBestPerson().getGeneCode();
                        break;
                    case VALIDATION_BEST:

                        network = (Network) stage.getValidationBestPerson().getGeneCode();
                        break;
                }

                EvaluationResult testResult = evaluation(problem.getTestingDataset());

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

                executionResponses[threadCount.getAndIncrement()] = new ExecutionResponse(network, testResult);

                if (threadCount.get() == THREADS) {
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

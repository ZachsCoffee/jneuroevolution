package execution;

import core.layer.TrainableLayer;
import evolution_builder.Evolution;
import evolution_builder.population.PersonMigration;
import evolution_builder.population.Population;
import execution.common.GenericProblem;
import execution.common.ProgressListener;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GenericProblemExecutor<P extends TrainableLayer, T extends GenericProblem<P>> implements ProgressListener {
    private final AtomicInteger progressCounter = new AtomicInteger();
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
    private final T problem;
    private HashMap<Long, Integer> threadToDatasetMap = null;

    private int totalEpochs;

    public GenericProblemExecutor(T problem) {
        this.problem = problem;
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

                GenericState<P> stage = new GenericState<>();

                stage.setProgressListener(this);

                if (this.personMigration) stage.setMigration(personMigration);

                evolution.startEvolution(epochs, stage);

                P trainableLayer = evolution.getTotalBestPerson().getGeneCode();

                evaluation(trainableLayer);

            }).start();
        }
    }

    protected abstract void evaluation(P bestPerson);

    @Override
    public void epochUpdate(int currentEpoch) {
    }

    @Override
    public void evolutionBestUpdate(double bestPersonFitness) {
        System.out.println("Best fitness: " + bestPersonFitness);
    }
}

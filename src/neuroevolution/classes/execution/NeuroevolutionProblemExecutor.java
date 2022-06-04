package execution;

import execution.common.DataBinder;
import data_manipulation.Dataset;
import data_manipulation.ProblemDatasets;
import evolution_builder.Evolution;
import evolution_builder.population.PersonManager;
import evolution_builder.population.PersonMigration;
import evolution_builder.population.Population;

import java.util.HashMap;
import maths.Maths;
import networks.interfaces.Network;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class NeuroevolutionProblemExecutor<P> extends Problem<P> implements Stage.ProgressListener {
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

    private final DataBinder dataBinder;
    private final AtomicInteger progressCounter = new AtomicInteger();
    private ProblemDatasets[] problemDatasets = null;
    private HashMap<Long, Integer> threadToDatasetMap = null;
    private double[] evolutionBest; // holds the (best person fitness) for each subpopulation
    private double meanFitness = 0; // the mean (best person fitness) from all the subpopulation

    private int totalEpochs;

    public NeuroevolutionProblemExecutor(NeuroevolutionPersonManager<P> personManager, DataBinder dataBinder) {
        super(personManager);

        if (dataBinder == null) throw new IllegalArgumentException(
                "Argument dataBinder not null"
        );

        this.dataBinder = dataBinder;
    }

    public abstract EvaluationResult evaluation(Network network, Dataset dataset);

    public abstract void executionEnds(ExecutionResponse[] responses);

    public double getMeanFitness() {
        return meanFitness;
    }
    
    @Override
    public Dataset getTrainingDataset() {
        return trainingDataset;
    }

    @Override
    public Dataset getValidationDataset() {
        return validationDataset;
    }

    @Override
    public Dataset getTestingDataset() {
        return testingDataset;
    }

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
        for (int i=0; i<THREADS; i++) {
            

            final int internalI = i;

            new Thread(() -> {
                
                long threadId = Thread.currentThread().getId();
                
                threadToDatasetMap.put(threadId, internalI);
                
                Evolution evolution = new Evolution(Population.generate(getPersonManager(), POPULATION_SIZE), PERCENT_OF_FITNESS, this);

                if (PERSON_MIGRATION) personMigration.addPopulation(evolution);

                Stage stage = new Stage(this, EPOCHS);

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

                EvaluationResult testResult = evaluation(network, getTestingDataset());
                
                dataBinder.addResults(new ResultsData(
                        stage.getEvolutionStatistics(),
                        stage.getValidationStatistics(),
                        getTestingDataset().targets,
                        testResult.predictionValues,
                        String.format(
                                "[ Evaluation: %.7f ] [ Thread ID: %d ] [ Validation: %.7f ] [ Training: %.7f ]",
                                testResult.evaluationError, threadId, stage.getValidationBestFitness(), evolution.getTotalBestPerson().getFitness()
                        )
                ));

                executionResponses[threadCount.getAndIncrement()] = new ExecutionResponse(network, testResult);

                if (threadCount.get() == THREADS) {
                    executionEnds(executionResponses);
                    System.out.printf("Total execution time: %.2fs", (System.currentTimeMillis() - startTimestamp) / 1000d);
                }
            }).start();
        }
    }
    
    @Override
    public void epochUpdate(int currentEpoch) {
        dataBinder.addProgress((int) Maths.percent(totalEpochs, progressCounter.incrementAndGet()));
    }

    protected ExecutionResponse findBest(ExecutionResponse[] executionResponses) {
        ExecutionResponse bestExecutionResponse = executionResponses[0];

        for (int i=1; i<executionResponses.length; i++) {
            if (executionResponses[i].evaluationResult.evaluationError < bestExecutionResponse.evaluationResult.evaluationError) {
                bestExecutionResponse = executionResponses[i];
            }
        }

        return bestExecutionResponse;
    }

    @Override
    public final synchronized void evolutionBestUpdate(double bestPersonFitness) {
        int bestPersonPos = threadToDatasetMap.get(Thread.currentThread().getId());
        
        evolutionBest[bestPersonPos] = bestPersonFitness;
        double sum = 0;
        for (double bestFitness : evolutionBest) {
            sum += bestFitness;
        }
        
        meanFitness = sum / evolutionBest.length;
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

import data_manipulation.Dataset;
import evolution_builder.Evolution;
import evolution_builder.population.PersonMigration;
import evolution_builder.population.Population;
import execution.EvaluationTarget;
import maths.Maths;
import networks.interfaces.Network;
import neuroevolution.Problem;
import neuroevolution.Stage;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ProblemExecutor extends Problem implements Stage.ProgressListener {
    protected EvaluationTarget evaluationTarget;
    protected boolean
            PERCENT_OF_FITNESS = false,
            PERSON_MIGRATION = true;

    protected double
            MIGRATION_PERCENT = 0.1;

    protected int
            THREADS = 5,
            EPOCHS = 2000,
            POPULATION_SIZE = 40;

    private DataBinder dataBinder;
    private AtomicInteger progressCounter = new AtomicInteger();

    private int totalEpochs;

    public ProblemExecutor(DataBinder dataBinder) {

        if (dataBinder == null) throw new IllegalArgumentException(
                "Argument dataBinder not null"
        );

        this.dataBinder = dataBinder;
    }

    public void execute() {

        progressCounter.set(0);
        // compute the total epochs
        totalEpochs = THREADS * EPOCHS;

        PersonMigration personMigration = new PersonMigration(MIGRATION_PERCENT, EPOCHS, THREADS);

        for (int i=0; i<THREADS; i++) {

            new Thread(() -> {

                Population population = new Population(getPersonManager(), POPULATION_SIZE);
                population.createPopulation();

                Evolution evolution = new Evolution(population, PERCENT_OF_FITNESS, this);

                if (PERSON_MIGRATION) personMigration.addPopulation(evolution);

                Stage stage = new Stage(this, EPOCHS);

                stage.setProgressListener(this);

                if (PERSON_MIGRATION) stage.setMigration(personMigration);

                evolution.startEvolution(EPOCHS, stage);

                Network network = null;
                switch (evaluationTarget) {
                    case EVOLUTION_BEST:

                        network = (Network) evolution.getTotalBestPerson().getGeneCode();
                        break;
                    case VALIDATION_BEST:

                        network = (Network) stage.getValidationBestPerson().getGeneCode();
                        break;
                }

                EvaluationResult testResult = evaluation(network, testingDataset);

                dataBinder.addResults(new ResultsData(
                        stage.getEvolutionStatistics(),
                        stage.getValidationStatistics(),
                        testingDataset.targets,
                        testResult.predictionValues,
                        String.format(
                                "[ Evaluation: %.7f ] [ Validation: %.7f ] [ Training: %.7f ]",
                                testResult.evaluationError, 20 - stage.getValidationBestFitness(), 20 - evolution.getTotalBestPerson().getFitness()
                        )
                ));

            }).start();
        }
    }

    @Override
    public void epochUpdate(int currentEpoch) {
        dataBinder.addProgress((int) Maths.percent(totalEpochs, progressCounter.incrementAndGet()));
    }

    public abstract EvaluationResult evaluation(Network network, Dataset testingDataset);

    public static class EvaluationResult {

        public double[][] predictionValues;
        public double evaluationError;

        public EvaluationResult(double[][] predictionValues, double evaluationError) {
            this.predictionValues = predictionValues;
            this.evaluationError = evaluationError;
        }
    }
}

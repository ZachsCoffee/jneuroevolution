package basic_gui;

import data_manipulation.CrossValidation;
import data_manipulation.Dataset;
import data_manipulation.DatasetSpecs;
import data_manipulation.ProblemDatasets;
import evolution_builder.Evolution;
import evolution_builder.population.PersonMigration;
import evolution_builder.population.Population;
import execution.EvaluationTarget;
import files.csv.CSVFileReader;
import files.csv.CSVFileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import maths.Maths;
import networks.interfaces.Network;
import neuroevolution.Problem;
import neuroevolution.Stage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class ProblemExecutor extends Problem implements Stage.ProgressListener {
    protected EvaluationTarget EVALUATION_TARGET;
    protected boolean
            PERCENT_OF_FITNESS = false,
            PERSON_MIGRATION = true,
            CROSS_VALIDATION_LEARNING = false;

    protected double
            MIGRATION_PERCENT = 0.1;

    protected int
            THREADS = 5,
            EPOCHS = 2000,
            POPULATION_SIZE = 40;

    private DataBinder dataBinder;
    private DatasetSpecs crossValidationSpecs;
    private AtomicInteger progressCounter = new AtomicInteger();
    private ProblemDatasets[] problemDatasetses = null;
    private HashMap<Long, Integer> threadToDatasetMap = null;
    private double[] evolutionBest; // holds the (best person fitness) for each subpopulation
    private double meanFitness = 0; // the mean (best person fitness) from all the subpopulation
    private double[][] data;

    private int totalEpochs;

    public ProblemExecutor(DataBinder dataBinder) {

        if (dataBinder == null) throw new IllegalArgumentException(
                "Argument dataBinder not null"
        );

        this.dataBinder = dataBinder;
    }

    public abstract EvaluationResult evaluation(Network network, Dataset dataset);

    public double getMeanFitness() {
        return meanFitness;
    }
    
    
    @Override
    public Dataset getTrainingDataset() {
        if (CROSS_VALIDATION_LEARNING) {
            int datasetPos = threadToDatasetMap.get(Thread.currentThread().getId());
            return problemDatasetses[datasetPos].getTrainingDataset();
        }

        return trainingDataset;
    }

    @Override
    public Dataset getValidationDataset() {
        if (CROSS_VALIDATION_LEARNING) {
            int datasetPos = threadToDatasetMap.get(Thread.currentThread().getId());
            return problemDatasetses[datasetPos].getValidationDataset();
        }

        return validationDataset;
    }

    @Override
    public Dataset getTestingDataset() {
        if (CROSS_VALIDATION_LEARNING) {
            int datasetPos = threadToDatasetMap.get(Thread.currentThread().getId());
            return problemDatasetses[datasetPos].getTestingDataset();
        }

        return testingDataset;
    }
    
    public void setCrossValidationLearning(double[][] data) {
//        if (crossValidationSpecs == null) throw new IllegalArgumentException(
//                "crossValidationSpecs not null"
//        );
        this.data = data;
        
//        this.crossValidationSpecs = crossValidationSpecs;
        CROSS_VALIDATION_LEARNING = true;
    }
    Double test = 0D;
    public void execute() {

        progressCounter.set(0);
        // compute the total epochs
        totalEpochs = THREADS * EPOCHS;

        evolutionBest = new double[THREADS];

        threadToDatasetMap = new HashMap<>();

        PersonMigration personMigration = new PersonMigration(MIGRATION_PERCENT, EPOCHS, THREADS);

        final String resultsFolder = DateTimeFormatter.ofPattern("dd_MM_yyy__HH_mm_ss").format(LocalDateTime.now());
        try {
            Files.createDirectory(Paths.get("./src/gr/polar/titanic/data/"+resultsFolder));
        } 
        catch (IOException ex) {
            System.err.println("Folder not created!");
        }
        
        if (CROSS_VALIDATION_LEARNING) {
            problemDatasetses = CrossValidation.kFolds(data, THREADS, 0.25);            
//            problemDatasetses = CrossValidation.custom(DatasetSpecs.init(data)
//                    .setTrainingSize(.5)
//                    .setValidationSize(0.25)
//                    .setTestingSize(0.25).setup(), THREADS);

        }
        
        AtomicLong meanSum = new AtomicLong(0);
        AtomicInteger threadCount = new AtomicInteger(1);
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

//                customCode(resultsFolder, threadId, network);
                
                EvaluationResult testResult = evaluation(network, getTestingDataset());
                
                synchronized(test) {
                    test += testResult.evaluationError;
                }
                    
                if (threadCount.get() == THREADS) {
                    System.err.println("Cross validation error: "+(test / THREADS));
                }
                
                threadCount.incrementAndGet();
                
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

            }).start();
        }
    }

    public void customCode(String resultsFolder, long thread, Network network) {
        double[][] testingData = CSVFileReader.readNumbersFile("./src/gr/polar/titanic/data/normal_test6.csv", ",");
        double[][] ids = CSVFileReader.readNumbersFile("./src/gr/polar/titanic/data/passenger_id.csv", ",");
        
        CSVFileWriter csvFileWriter = new CSVFileWriter("./src/gr/polar/titanic/data/"+resultsFolder+"/results_"+thread+".csv");
        
        for (int i=0; i<testingData.length; i++) {
//            System.out.println(Math.round(network.compute(testingData[i])[0]) >= 0 ? 1 : 0);
            
//            csvFileWriter.writeLine(ids[i][0], Math.round(network.compute(testingData[i])[0]) >= 0 ? 1 : 0);
        }
        
        csvFileWriter.close();
    }
    
    @Override
    public void epochUpdate(int currentEpoch) {
        dataBinder.addProgress((int) Maths.percent(totalEpochs, progressCounter.incrementAndGet()));
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
//        System.err.println(meanFitness);
//        meanFitness = 0;
    }
    
    public static class EvaluationResult {

        public double[][] predictionValues;
        public double evaluationError;

        public EvaluationResult(double[][] predictionValues, double evaluationError) {
            this.predictionValues = predictionValues;
            this.evaluationError = evaluationError;
        }
    }
}

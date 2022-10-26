package simple;

import data_manipulation.DatasetSpecs;
import data_manipulation.DatasetSplitter;
import data_manipulation.ProblemDatasets;
import data_manipulation.RawDataset;
import evolution_builder.components.Mutation;
import evolution_builder.components.Recombination;
import evolution_builder.components.Selection;
import evolution_builder.population.Population;
import execution.AbstractNeuroevolutionProblem;
import execution.common.NeuroevolutionPersonManager;
import execution.Problem;
import files.csv.CSVFileReader;
import functions.ActivationFunction;
import maths.MinMax;
import networks.interfaces.PartialNetwork;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;
import neuroevolution.NeuroevolutionGenes;
import neuroevolution.NeuroevolutionGenesOptions;
import neuroevolution.NeuroevolutionNetworkPersonManager;

public class AustralianCreditProblem extends AbstractNeuroevolutionProblem<PartialNetwork, RawDataset> {

    public static final int EPOCHS = 500;

    private final NeuroevolutionNetworkPersonManager<PartialNetwork> personManager = new NeuroevolutionNetworkPersonManager<>(
        this);
    private final NeuroevolutionGenes<PartialNetwork> genes = new NeuroevolutionGenes<>(
        NeuroevolutionGenesOptions.Builder.getInstance()
            .setDeactivateWeightMutationChange(.2)
            .setWeightStatusMutationChance(.2)
            .build()
    );

    public AustralianCreditProblem() {
        setDynamicMutation(new MinMax(500, 1000), EPOCHS);

        double[][] data = CSVFileReader.readNumbersFile(
            "/home/zachs/Develop/Java/artificialintelligence/datasets/australian_credit/output/australian_credit_norm.csv",
            ","
        );

        ProblemDatasets datasets = DatasetSplitter.split(
            DatasetSpecs.init(data)
                .setTrainingSize(.9)
                .setTestingSize(.1)
                .setup()
        );

        trainingDataset = datasets.getTrainingDataset();
//        validationDataset = datasets.getValidationDataset();
        testingDataset = datasets.getTestingDataset();
    }

    @Override
    public NeuroevolutionPersonManager<PartialNetwork> getPersonManager() {
        return personManager;
    }

    @Override
    public void computePercentOfFitness(Population<PartialNetwork> population) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Population<PartialNetwork> recombinationOperator(Population<PartialNetwork> population, int epoch) {
        return Recombination.random(population, 3, genes);
    }

    @Override
    public Population<PartialNetwork> selectionMethod(Population<PartialNetwork> population) {
        return Selection.tournament(population, 5, false);
    }

    @Override
    public void mutationMethod(Population<PartialNetwork> population, int epoch, int maxEpoch) {
        Mutation.mutation(population, getMutationChange(epoch), 2, false, genes);
    }

    @Override
    public PartialNetwork buildNetwork(int maxStartValue) {
        return NeuralNetworkBuilder.initialize(14)
            .addLayer(20, ActivationFunction.SIGMOID.getFunction())
            .addLayer(10, ActivationFunction.SIGMOID.getFunction())
            .addLayer(10, ActivationFunction.SIGMOID.getFunction())
            .addLayer(1, ActivationFunction.SIGMOID.getFunction())
            .build();
    }

    @Override
    public PartialNetwork buildRandomNetwork(int maxStartValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double evaluateNetwork(PartialNetwork network, RawDataset rawDataset) {
        int fitness = 0;

        for (int i = 0; i < rawDataset.SIZE; i++) {
            double[] results = network.compute(rawDataset.getFeatures()[i]);

            if (Math.round(results[0]) == rawDataset.getTargets()[i][0]) {
                fitness++;
            }
        }

        return (double) fitness / rawDataset.SIZE;
    }

    @Override
    public Problem<PartialNetwork, RawDataset> getProblem() {
        return this;
    }
}

package simple;

import data_manipulation.DatasetSpecs;
import data_manipulation.DatasetSplitter;
import data_manipulation.ProblemDatasets;
import data_manipulation.RawDataset;
import evolution_builder.components.Mutation;
import evolution_builder.components.Recombination;
import evolution_builder.components.Selection;
import evolution_builder.population.Population;
import execution.common.NeuroevolutionPersonManager;
import execution.Problem;
import files.csv.CSVFileReader;
import functions.ActivationFunction;
import maths.MinMax;
import networks.interfaces.Network;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;
import neuroevolution.NeuroevolutionGenes;
import neuroevolution.NeuroevolutionNetworkPersonManager;

public class AustralianCreditProblem extends Problem<Network, RawDataset> implements execution.common.NeuroevolutionProblem<Network> {

    public static final int EPOCHS = 500;

    private final NeuroevolutionNetworkPersonManager<Network> personManager = new NeuroevolutionNetworkPersonManager<>(
        this);
    private final NeuroevolutionGenes<Network> genes = new NeuroevolutionGenes<>();

    public AustralianCreditProblem() {
        setDynamicMutation(new MinMax(150, 500), EPOCHS);

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
    public NeuroevolutionPersonManager<Network> getPersonManager() {
        return personManager;
    }

    @Override
    public void computePercentOfFitness(Population<Network> population) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Population<Network> recombinationOperator(Population<Network> population, int epoch) {
        return Recombination.random(population, 3, genes);
    }

    @Override
    public Population<Network> selectionMethod(Population<Network> population) {
        return Selection.tournament(population, 5, false);
    }

    @Override
    public void mutationMethod(Population<Network> population, int epoch, int maxEpoch) {
        Mutation.mutation(population, getMutationChange(epoch), 2, true, genes);
    }

    @Override
    public Network buildNetwork(int maxStartValue) {
        return NeuralNetworkBuilder.initialize(14)
            .addLayer(20, ActivationFunction.SIGMOID.getFunction())
            .addLayer(10, ActivationFunction.SIGMOID.getFunction())
            .addLayer(10, ActivationFunction.SIGMOID.getFunction())
            .addLayer(1, ActivationFunction.SIGMOID.getFunction())
            .build();
    }

    @Override
    public Network buildRandomNetwork(int maxStartValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double evaluateNetwork(Network network, RawDataset rawDataset) {
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
    public Problem<Network, RawDataset> getProblem() {
        return this;
    }
}

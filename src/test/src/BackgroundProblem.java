import basic_gui.DataBinder;
import basic_gui.ProblemExecutor;
import data_manipulation.Dataset;
import evolution_builder.components.Mutation;
import evolution_builder.components.PercentOfFitness;
import evolution_builder.components.Recombination;
import evolution_builder.components.Selection;
import evolution_builder.population.Population;
import files.CSVFileReader;
import functions.ActivationFunctions;
import maths.Function;
import networks.interfaces.Network;
import networks.multilayer_perceptron.NetworkLayer;
import networks.multilayer_perceptron.NeuralNetwork;
import neuroevolution.Problem;

import java.io.File;

public class BackgroundProblem extends ProblemExecutor {

    public BackgroundProblem(DataBinder dataBinder) {
        super(dataBinder);

        double[][] allData = CSVFileReader.readNumbersFile(
                new File("/home/zachs/Develop/Java/artificialintelligence/datasets/dataset2.csv"), ","
        );
    }

    @Override
    public EvaluationResult evaluation(Network network, Dataset testingDataset) {
        return null;
    }

    @Override
    public void computePercentOfFitness(Population population) {
        PercentOfFitness.percentFromCurrentBestRanked(population);
    }

    @Override
    public Population recombinationOperator(Population population, int epoch) {
        return Recombination.randomWithFilter(population, 10, PERSON);
    }

    @Override
    public Population selectionMethod(Population population) {
        return Selection.tournament(population, 3, PERCENT_OF_FITNESS);
    }

    @Override
    public void mutationMethod(Population population, int epoch, int maxEpoch) {
        Mutation.mutation(population, getMutationChange(epoch), 5, true, PERSON);
    }

    @Override
    public Network buildNetwork(int maxStartValue) {
        hiddenLayerFunction = ActivationFunctions.logsig();
        Function middleFunction = ActivationFunctions.gauss();
        outputLayerFunction = ActivationFunctions.sigmoid();

        NetworkLayer[] layers = new NetworkLayer[5];
        layers[0] = new NetworkLayer(5, 12, hiddenLayerFunction);
        layers[1] = new NetworkLayer(50, 5, hiddenLayerFunction);
        layers[2] = new NetworkLayer(30, 50, middleFunction);
        layers[3] = new NetworkLayer(10, 30, hiddenLayerFunction);
        layers[4] = new NetworkLayer(1, 10, outputLayerFunction);

        return new NeuralNetwork(layers, maxStartValue);
    }

    @Override
    public Network buildRandomNetwork(int maxStartValue) {
        return null;
    }

    @Override
    public double evaluateNetwork(Network network, Dataset dataset) {
        return 0;
    }

    @Override
    public Problem getProblem() {
        return this;
    }
}

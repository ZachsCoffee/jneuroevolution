/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import data_manipulation.RawDataset;
import data_manipulation.DatasetType;
import evolution_builder.population.PopulationPerson;
import execution.NeuroevolutionPersonManager;
import execution.NeuroevolutionProblem;
import networks.interfaces.Network;
import networks.interfaces.TimeNetwork;
import maths.Function;
import networks.multilayer_perceptron.optimizer.BackpropagationMLP;
import networks.multilayer_perceptron.network.NetworkLayer;
import networks.multilayer_perceptron.network.NeuralNetwork;

import java.util.Objects;

/**
 * @author Zachs
 */
public class NeuroevolutionNetworkPersonManager<P> implements NeuroevolutionPersonManager<Network> {

    private final NeuroevolutionProblem<P> neuroevolutionProblem;

    public NeuroevolutionNetworkPersonManager(NeuroevolutionProblem<P> neuroevolutionProblem) {
        if (neuroevolutionProblem == null) throw new IllegalArgumentException("Argument mlProblem not null!");

        this.neuroevolutionProblem = Objects.requireNonNull(neuroevolutionProblem);
    }

    @Override
    public PopulationPerson<Network> newPerson() {
        return new PopulationPerson<>(
            neuroevolutionProblem.buildNetwork(NeuroevolutionGenes.maxStartValue)
        );
    }

    @Override
    public PopulationPerson<Network> newRandomPerson() {
        return new PopulationPerson<>(
            neuroevolutionProblem.buildRandomNetwork(NeuroevolutionGenes.maxStartValue)
        );
    }

    @Override
    public PopulationPerson<Network> newSameLengthAs(PopulationPerson<Network> populationPerson) {
        NeuralNetwork givenNetwork = (NeuralNetwork) populationPerson.getGeneCode();

        NetworkLayer[] networkLayers = new NetworkLayer[givenNetwork.getLayerCount()];

        NetworkLayer firstLayer = givenNetwork.getLayerAt(0);
        Function firstLayerFunction = firstLayer.getFunction();
        int pastLayerNeurons = firstLayer.getNeuronsCount();

        if (firstLayerFunction != null) {
            networkLayers[0] = new NetworkLayer(
                pastLayerNeurons,
                givenNetwork.getLayerAt(0).getNeuronAt(0).getInputsCount(),
                firstLayerFunction
            );
        }
        else {
            networkLayers[0] = new NetworkLayer(
                pastLayerNeurons,
                givenNetwork.getLayerAt(0).getNeuronAt(0).getInputsCount()
            );
        }

        NetworkLayer tempLayer;
        Function tempLayerFunction;
        for (int i = 1; i < networkLayers.length; i++) {
            tempLayer = givenNetwork.getLayerAt(i);
            tempLayerFunction = tempLayer.getFunction();

            if (tempLayerFunction != null) {
                networkLayers[i] = new NetworkLayer(
                    tempLayer.getNeuronsCount(),
                    pastLayerNeurons,
                    tempLayer.getFunction()
                );
            }
            else {
                networkLayers[i] = new NetworkLayer(tempLayer.getNeuronsCount(), pastLayerNeurons);
            }

            pastLayerNeurons = tempLayer.getNeuronsCount();
        }

        if (givenNetwork instanceof BackpropagationMLP) {
            return new PopulationPerson<>(
                new BackpropagationMLP(
                    networkLayers,
                    ((BackpropagationMLP) givenNetwork).LEARN_RATE,
                    neuroevolutionProblem.getProblem().getTrainingDataset().features,
                    neuroevolutionProblem.getProblem().getTrainingDataset().targets
                )
            );
        }
        else {
            return new PopulationPerson<>(new NeuralNetwork(networkLayers, 2));
        }
    }

    @Override
    public double computeFitness(PopulationPerson<Network> populationPerson, DatasetType datasetType) {
        return computeFitness(populationPerson, getDataset(datasetType));
    }

    private RawDataset getDataset(DatasetType datasetType) {
        switch (datasetType) {
            case TRAINING:
                return neuroevolutionProblem.getProblem().getTrainingDataset();
            case VALIDATION:
                return neuroevolutionProblem.getProblem().getValidationDataset();
            case TESTING:
                return neuroevolutionProblem.getProblem().getTestingDataset();
            default:
                throw new UnsupportedOperationException("For rawDataset type: " + datasetType);
        }
    }

    private double computeFitness(PopulationPerson<Network> populationPerson, RawDataset rawDataset) {
        double fitness;

        if (populationPerson.getGeneCode() instanceof TimeNetwork) {
            TimeNetwork timeNetwork = (TimeNetwork) populationPerson.getGeneCode();
            timeNetwork.startCompute();

            fitness = neuroevolutionProblem.evaluateNetwork(timeNetwork, rawDataset);

            timeNetwork.endCompute();
        }
        else {
            Network network = (Network) populationPerson.getGeneCode();
            //krataw thn 8esh tou target apo to training set

            fitness = neuroevolutionProblem.evaluateNetwork(network, rawDataset);
        }

        return fitness;
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import core.layer.TrainableLayer;
import data_manipulation.RawDataset;
import data_manipulation.DatasetType;
import evolution_builder.population.PopulationPerson;
import execution.common.NeuroevolutionPersonManager;
import execution.common.NeuroevolutionProblem;
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
public class NeuroevolutionNetworkPersonManager<P extends Network> implements NeuroevolutionPersonManager<P> {

    private final NeuroevolutionProblem<P> neuroevolutionProblem;

    public NeuroevolutionNetworkPersonManager(NeuroevolutionProblem<P> neuroevolutionProblem) {
        if (neuroevolutionProblem == null) throw new IllegalArgumentException("Argument mlProblem not null!");

        this.neuroevolutionProblem = Objects.requireNonNull(neuroevolutionProblem);
    }

    @Override
    public PopulationPerson<P> newPerson() {
        return new PopulationPerson<P>(
            neuroevolutionProblem.buildNetwork(NeuroevolutionGenes.maxStartValue)
        );
    }

    @Override
    public PopulationPerson<P> newRandomPerson() {
        return new PopulationPerson<>(
            neuroevolutionProblem.buildRandomNetwork(NeuroevolutionGenes.maxStartValue)
        );
    }

    @Override
    public PopulationPerson<P> newSameLengthAs(PopulationPerson<P> populationPerson) {
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
            PopulationPerson<P> populationPerson1 = new PopulationPerson<>(
                (P)new BackpropagationMLP(
                    networkLayers,
                    ((BackpropagationMLP) givenNetwork).LEARN_RATE,
                    neuroevolutionProblem.getProblem().getTrainingDataset().getFeatures(),
                    neuroevolutionProblem.getProblem().getTrainingDataset().getTargets()
                )
            );
            return populationPerson1;
        }
        else {
            return new PopulationPerson<>((P)new NeuralNetwork(networkLayers, 2));
        }
    }

    @Override
    public double computeFitness(PopulationPerson<P> populationPerson, DatasetType datasetType) {
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

    private double computeFitness(PopulationPerson<P> populationPerson, RawDataset rawDataset) {
        double fitness;

        if (populationPerson.getGeneCode() instanceof TimeNetwork) {
            TimeNetwork timeNetwork = (TimeNetwork) populationPerson.getGeneCode();
            timeNetwork.startCompute();

            fitness = neuroevolutionProblem.evaluateNetwork((P) timeNetwork, rawDataset);

            timeNetwork.endCompute();
        }
        else {
            fitness = neuroevolutionProblem.evaluateNetwork(populationPerson.getGeneCode(), rawDataset);
        }

        return fitness;
    }
}
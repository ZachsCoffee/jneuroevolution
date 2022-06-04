/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import data_manipulation.Dataset;
import data_manipulation.DatasetType;
import evolution_builder.population.Person;
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
public class NeuroevolutionPerson<P> implements NeuroevolutionPersonManager<Network> {

    private final NeuroevolutionProblem<P> neuroevolutionProblem;

    public NeuroevolutionPerson(NeuroevolutionProblem<P> neuroevolutionProblem) {
        if (neuroevolutionProblem == null) throw new IllegalArgumentException("Argument mlProblem not null!");

        this.neuroevolutionProblem = Objects.requireNonNull(neuroevolutionProblem);
    }

    @Override
    public Person<Network> newPerson() {
        return new Person<>(
            neuroevolutionProblem.buildNetwork(NeuroevolutionGenes.maxStartValue)
        );
    }

    @Override
    public Person<Network> newRandomPerson() {
        return new Person<>(
            neuroevolutionProblem.buildRandomNetwork(NeuroevolutionGenes.maxStartValue)
        );
    }

    @Override
    public Person<Network> newSameLengthAs(Person<Network> person) {
        NeuralNetwork givenNetwork = (NeuralNetwork) person.getGeneCode();

        NetworkLayer[] networkLayers = new NetworkLayer[givenNetwork.getLayerCount()];

        NetworkLayer firstLayer = givenNetwork.getLayerAt(0);
        Function firstLayerFunction = firstLayer.getFunction();
        int pastLayerNeurons = firstLayer.getNeuronsCount();

        if (firstLayerFunction != null) {
//            if (givenNetwork.getLayerAt(0).getNeuronAt(0).getInputsCount()-1 == 6) throw new RuntimeException("AAAA");
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

        // TODO: make generic the network type e.g backpropagationmlp
        if (givenNetwork instanceof BackpropagationMLP) {
            return new Person<>(
                new BackpropagationMLP(
                    networkLayers,
                    ((BackpropagationMLP) givenNetwork).LEARN_RATE,
                    neuroevolutionProblem.getProblem().getTrainingDataset().features,
                    neuroevolutionProblem.getProblem().getTrainingDataset().targets
                )
            );
        }
        else {
            return new Person<>(new NeuralNetwork(networkLayers, 2));
        }
    }

    @Override
    public double computeFitness(Person<Network> person, DatasetType datasetType) {
        return computeFitness(person, getDataset(datasetType));
    }

    private Dataset getDataset(DatasetType datasetType) {
        switch (datasetType) {
            case TRAINING:
                return neuroevolutionProblem.getProblem().getTrainingDataset();
            case VALIDATION:
                return neuroevolutionProblem.getProblem().getValidationDataset();
            case TESTING:
                return neuroevolutionProblem.getProblem().getTestingDataset();
            default:
                throw new UnsupportedOperationException("For dataset type: " + datasetType);
        }
    }

    private double computeFitness(Person<Network> person, Dataset dataset) {
        double fitness;

        if (person.getGeneCode() instanceof TimeNetwork) {
            TimeNetwork timeNetwork = (TimeNetwork) person.getGeneCode();
            timeNetwork.startCompute();

            fitness = neuroevolutionProblem.evaluateNetwork(timeNetwork, dataset);

            timeNetwork.endCompute();
        }
        else {
            Network network = (Network) person.getGeneCode();
            //krataw thn 8esh tou target apo to training set

            fitness = neuroevolutionProblem.evaluateNetwork(network, dataset);
        }

        return fitness;
    }
}
package rover;

import data_manipulation.DatasetType;
import evolution_builder.population.PersonManager;
import evolution_builder.population.PopulationPerson;
import execution.common.GenericProblem;
import execution.common.NeuroevolutionPersonManager;
import maths.Function;
import networks.interfaces.Network;
import networks.multilayer_perceptron.network.NetworkLayer;
import networks.multilayer_perceptron.network.NeuralNetwork;


public class GenericNeuroevolutionManager<P extends Network> implements PersonManager<P>, NeuroevolutionPersonManager<P> {

    private final GenericProblem<P> genericProblem;

    public GenericNeuroevolutionManager(GenericProblem<P> genericProblem) {
        this.genericProblem = genericProblem;
    }

    @Override
    public PopulationPerson<P> newRandomPerson() {
        return (PopulationPerson<P>) new PopulationPerson<>(
                genericProblem.buildNetwork(0)
        );
    }

    @Override
    public PopulationPerson<P> newPerson() {
        return (PopulationPerson<P>) new PopulationPerson<>(
                genericProblem.buildNetwork(0)
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

        return (PopulationPerson<P>) new PopulationPerson<>(new NeuralNetwork(networkLayers, 2));
    }

    @Override
    public double computeFitness(PopulationPerson<P> populationPerson) {
        return genericProblem.evaluateNetwork(populationPerson.getGeneCode(), null);
    }

    @Override
    public double computeFitness(PopulationPerson<P> populationPerson, DatasetType datasetType) {
        throw new UnsupportedOperationException("Compute fitness with dataset type isn't supported.");
    }
}

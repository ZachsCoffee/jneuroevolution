/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import data_manipulation.Dataset;
import networks.interfaces.Network;
import networks.interfaces.TimeNetwork;
import evolution_builder.population.PersonManager;
import maths.Function;
import java.util.Arrays;
import networks.multilayer_perceptron.BackpropagationMLP;
import networks.multilayer_perceptron.NetworkLayer;
import networks.multilayer_perceptron.NeuralNetwork;
/**
 *
 * @author Zachs
 */
class Person extends Genes implements PersonManager {
    private static MLProblem mlProblem;
    
    protected Person(MLProblem mlProblem){
        if (mlProblem == null) throw new IllegalArgumentException("Argument mlProblem not null!");
        
        Person.mlProblem = mlProblem;
    }
    
    @Override
    public evolution_builder.population.Person newPerson() {
        return new evolution_builder.population.Person(
            mlProblem.buildNetwork(Genes.maxStartValue)
        );
    }

    @Override
    public evolution_builder.population.Person newRandomPerson() {
        return new evolution_builder.population.Person(
            mlProblem.buildRandomNetwork(Genes.maxStartValue)
        );
    }
    
    @Override
    public evolution_builder.population.Person newSameLengthAs(evolution_builder.population.Person person) {
        NeuralNetwork givenNetwork = (NeuralNetwork) person.getGeneCode();
        
        NetworkLayer[] networkLayers = new NetworkLayer[givenNetwork.getLayerCount()];
        
        NetworkLayer firstLayer = givenNetwork.getLayerAt(0);
        Function firstLayerFunction = firstLayer.getFunction();
        int pastLayerNeurons = firstLayer.getNeuronsCount();
        
        if (firstLayerFunction != null){
//            if (givenNetwork.getLayerAt(0).getNeuronAt(0).getInputsCount()-1 == 6) throw new RuntimeException("AAAA");
            networkLayers[0] = new NetworkLayer(pastLayerNeurons, givenNetwork.getLayerAt(0).getNeuronAt(0).getInputsCount(), firstLayerFunction);
        }
        else{
            networkLayers[0] = new NetworkLayer(pastLayerNeurons, givenNetwork.getLayerAt(0).getNeuronAt(0).getInputsCount());
        }
        
        NetworkLayer tempLayer;
        Function tempLayerFunction;
        for (int i=1; i<networkLayers.length; i++){
            tempLayer = givenNetwork.getLayerAt(i);
            tempLayerFunction = tempLayer.getFunction();
            
            if (tempLayerFunction != null){
                networkLayers[i] = new NetworkLayer(tempLayer.getNeuronsCount(), pastLayerNeurons, tempLayer.getFunction());
            }
            else{
                networkLayers[i] = new NetworkLayer(tempLayer.getNeuronsCount(), pastLayerNeurons);
            }
            
            pastLayerNeurons = tempLayer.getNeuronsCount();
        }
        
        return new evolution_builder.population.Person(new NeuralNetwork(networkLayers, 20));
//        return new evolution_builder.population.Person(
//                new BackpropagationMLP(
//                        networkLayers, 
//                        5, 
//                        mlProblem.getProblem().getTrainingDataset(), 
//                        mlProblem.getProblem().getTrainingData()
//                )
//        );
    }
    
    @Override
    public double computeFitness(evolution_builder.population.Person person) {

        return computeFitness(person, mlProblem.getProblem().getTrainingDataset());
    }

    static double computeFitness(evolution_builder.population.Person person, Dataset dataset){
        double fitness;
        
        if (person.getGeneCode() instanceof TimeNetwork){
            TimeNetwork timeNetwork = (TimeNetwork) person.getGeneCode();
            timeNetwork.startCompute();
            
            fitness = mlProblem.evaluateNetwork(timeNetwork, dataset);
            
            timeNetwork.endCompute();
        }
        else{
            Network network = (Network) person.getGeneCode();
            //krataw thn 8esh tou target apo to training set
            
            fitness = mlProblem.evaluateNetwork(network, dataset);
        }
        
        return fitness;
    }
    
//    private static double compute(Network network, double[][] dataSet){
//        //krataw thn 8esh tou target apo to training set
//        int target = dataSet[0].length -1;
//
//        double sum = 0, predictedValue;
//        for (int i=0; i<dataSet.length; i++) {
//            predictedValue = network.compute(Arrays.copyOf(dataSet[i], dataSet[i].length-1))[0];
//            sum += Math.pow(dataSet[i][target] - predictedValue, 2);
//        }
//
//        return 1 - Math.sqrt(sum/dataSet.length);
//    }
}
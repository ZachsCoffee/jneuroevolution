/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.multilayer_perceptron.optimizer;

import maths.Function;
import networks.multilayer_perceptron.network.NetworkLayer;
import networks.multilayer_perceptron.network.NeuralNetwork;

/**
 *
 * @author arx-dev-3a-19
 */
public class BackpropagationMLP extends NeuralNetwork {
    public final double LEARN_RATE;

    private final double[][] TRAINING_TARGETS, TRAINING_FEATURES;
    
    private double[][] signalsError, neurosOutput;//, outputDerivative;
    
    public BackpropagationMLP(NeuralNetwork neuralNetwork, double learnRate, double[][] trainingFeatures, double[][] trainingTargets){
        this(getNetworkLayers(neuralNetwork), learnRate, trainingFeatures, trainingTargets);
    }
    
    public BackpropagationMLP(NetworkLayer[] networkLayers, double learnRate, double[][] trainingFeatures, double[][] trainingTargets){
        super(networkLayers, 1);
        
        if (learnRate < 0 || learnRate > 1) throw new IllegalArgumentException(
                "learnRate must be a number between 0 and 1, learnRate="+learnRate
        );
        if (trainingFeatures == null) throw new IllegalArgumentException(
                "trainingSet not null"
        );
        if (trainingTargets == null) throw new IllegalArgumentException(
                "trainingData not null"
        );
        
        LEARN_RATE = learnRate;
        TRAINING_FEATURES = trainingFeatures;
        TRAINING_TARGETS = trainingTargets;
        
        signalsError = new double[layers.length][];
        neurosOutput = new double[layers.length][];
        
        for (int i=0; i<layers.length; i++){
            signalsError[i] = new double[layers[i].getNeuronsCount()];
            neurosOutput[i] = new double[layers[i].getNeuronsCount()];
        }
    }

    @Override
    public double[] compute(double[] features) {
        double[] results = features;
        for (int i=0; i<layers.length; i++){
            results = computeLayer(i, results);
            neurosOutput[i] = results;
        }
        
        return results;
    }
    
    public void propagate(double learningRate){
        final int POS_OF_OUTPUT_NEURONS = layers.length -1;
        final int OUTPUT_NEURONS_COUNT = layers[layers.length -1].getNeuronsCount();
        final int TARGET_POS = 0;
        
        for (int i=0; i<TRAINING_TARGETS.length; i++){
            //compute
            compute(TRAINING_FEATURES[i]);
//            compute(Arrays.copyOf(TRAINING_SET[i], TRAINING_SET[i].length -1));

            
            //error for the outputlayer
            for (int j=0; j<OUTPUT_NEURONS_COUNT; j++){
                signalsError[POS_OF_OUTPUT_NEURONS][j] = 
                        (neurosOutput[POS_OF_OUTPUT_NEURONS][j] - TRAINING_TARGETS[i][TARGET_POS]) * 
                        (1 - neurosOutput[POS_OF_OUTPUT_NEURONS][j]);
            }
            
            //error for the hidden layers
            for (int k=layers.length -2; k>=0; k--){
                for (int g=0; g<layers[k].getNeuronsCount(); g++){
                    signalsError[k][g] = computeLayerSum(k +1, g) * (1 - neurosOutput[k][g]);
                }
            }
            
            updateWeights(learningRate);
        }
    }
    
    public double[] getNetworkOutput(){
        return neurosOutput[neurosOutput.length -1];
    }
    
    public double getError(){
        double error = 0, prediction = getNetworkOutput()[0];
        final int TARGET_POS = 0;
        
        for (int i=0; i<TRAINING_TARGETS.length; i++){
            error += Math.pow(TRAINING_TARGETS[i][TARGET_POS] - prediction, 2);
        }
        
        return Math.sqrt(error/TRAINING_TARGETS.length);
    }
    
    public void propagate(){
        propagate(LEARN_RATE);
    }
    
    private void updateWeights(double learningRate){
        
        for (int i=1; i<layers.length; i++){
            for (int j=0; j<layers[i].getNeuronsCount(); j++){
                
                //for the weights of neurons
                for (int k=0; k<layers[i -1].getNeuronsCount(); k++){
                    layers[i].getNeuronAt(j).addToWeight(
                            -learningRate * neurosOutput[i -1][k] * signalsError[i][j], 
                            k
                    );
                }
                
                //for the bias
                layers[i].getNeuronAt(j).addToBias(-learningRate * signalsError[i][j]);
            }
        }
    }

    //the next layer have equal amount of weights per neuron as the neuron count of current layer
    private double computeLayerSum(int layerPos, int weightPos){
        final int NEURONS_COUNT = layers[layerPos].getNeuronsCount();
        double sum = 0;
        for (int i=0; i<NEURONS_COUNT; i++){
            sum += layers[layerPos].getNeuronAt(i).getWeightAt(weightPos) * signalsError[layerPos][i];
        }
        
        return sum;
    }
    
    private static NetworkLayer[] getNetworkLayers(NeuralNetwork neuralNetwork){
        NetworkLayer tempLayer;
        Function layerFunction;
        
        NetworkLayer[] networkLayers = new NetworkLayer[neuralNetwork.getLayerCount()];
        for (int i=0; i<networkLayers.length; i++){
            tempLayer = neuralNetwork.getLayerAt(i);
            layerFunction = tempLayer.getFunction();
            
            if (layerFunction != null){
                networkLayers[i] = new NetworkLayer(
                        tempLayer.getNeuronsCount(), 
                        tempLayer.getNeuronAt(0).getInputsCount(),
                        tempLayer.getFunction()
                );
            }
            else{
                networkLayers[i] = new NetworkLayer(
                        tempLayer.getNeuronsCount(), 
                        tempLayer.getNeuronAt(0).getInputsCount()
                );
            }
        }
        
        return networkLayers;
    }
}

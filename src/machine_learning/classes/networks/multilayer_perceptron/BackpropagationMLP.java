/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.multilayer_perceptron;

import maths.Function;

/**
 *
 * @author arx-dev-3a-19
 */
public class BackpropagationMLP extends NeuralNetwork{
    public final float LEARN_RATE;

    private final float[][] TRAINING_TARGETS;
    private final float[][] TRAINING_FEATURES;
    
    private float[][] signalsError;
    private float[][] neuronsOutput;//, outputDerivative;
    
    public BackpropagationMLP(NeuralNetwork neuralNetwork, float learnRate, float[][] trainingFeatures, float[][] trainingTargets){
        this(getNetworkLayers(neuralNetwork), learnRate, trainingFeatures, trainingTargets);
    }
    
    public BackpropagationMLP(NetworkLayer[] networkLayers, float learnRate, float[][] trainingFeatures, float[][] trainingTargets){
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
        
        signalsError = new float[layers.length][];
        neuronsOutput = new float[layers.length][];
        
        for (int i=0; i<layers.length; i++){
            signalsError[i] = new float[layers[i].getNeuronsCount()];
            neuronsOutput[i] = new float[layers[i].getNeuronsCount()];
        }
    }

    @Override
    public float[] compute(float[] features) {
        float[] results = features;
        for (int i=0; i<layers.length; i++){
//            System.err.println(results.length+" "+i+" "+layers.length);
            results = layers[i].computeLayer(results);
            neuronsOutput[i] = results;
        }
        
        return results;
    }
    
    public void propagate(float learningRate){
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
                        (neuronsOutput[POS_OF_OUTPUT_NEURONS][j] - TRAINING_TARGETS[i][TARGET_POS]) *
                        (1 - neuronsOutput[POS_OF_OUTPUT_NEURONS][j]);
            }
            
            //error for the hidden layers
            for (int k=layers.length -2; k>=0; k--){
                for (int g=0; g<layers[k].getNeuronsCount(); g++){
                    signalsError[k][g] = computeLayerSum(k +1, g) * (1 - neuronsOutput[k][g]);
                }
            }
            
            updateWeights(learningRate);
        }
    }
    
    public float[] getNetworkOutput(){
        return neuronsOutput[neuronsOutput.length -1];
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
    
    private void updateWeights(float learningRate){
        
        for (int i=1; i<layers.length; i++){
            for (int j=0; j<layers[i].getNeuronsCount(); j++){
                
                //for the weights of neurons
                for (int k=0; k<layers[i -1].getNeuronsCount(); k++){
                    layers[i].getNeuronAt(j).addToWeight(
                            (float) -learningRate * neuronsOutput[i -1][k] * signalsError[i][j],
                            k
                    );
                }
                
                //for the bias
                layers[i].getNeuronAt(j).addToBias(-learningRate * signalsError[i][j]);
            }
        }
    }

    //the next layer have equal amount of weights per neuron as the neuron count of current layer
    private float computeLayerSum(int layerPos, int weightPos){
        final int NEURONS_COUNT = layers[layerPos].getNeuronsCount();
        float sum = 0;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.multilayer_perceptron;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import functions.ActivationFunctions;
import maths.Function;
import networks.interfaces.Network;
import maths.MinMax;
import networks.representations.LayerImage;
import networks.representations.NetworkImage;

/**
 *
 * @author main
 */
public class NeuralNetwork implements Network {
    
    
    public static NeuralNetwork loadFromJson(String json) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        
        NetworkImage networkImage = gson.fromJson(json, NetworkImage.class);
        
        LayerImage[] layerImages = networkImage.getLayers();
        
        NetworkLayer[] networkLayers = new NetworkLayer[layerImages.length];
        
        // build the schema of the network
        for (int i=0; i<networkLayers.length; i++) {
            networkLayers[i] = new NetworkLayer(
                    layerImages[i].getNeuronsCount(), 
                    layerImages[i].getWeightsPerNeuron(), 
                    ActivationFunctions.getFunction(layerImages[i].getFunctionName())
            );
        }
        
        // fill the network with the weights
        NeuralNetwork neuralNetwork = new NeuralNetwork(networkLayers);
        int weightsCount = neuralNetwork.getWeightsCount();
        for (int i=0; i<weightsCount; i++) {
            neuralNetwork.setWeightAt(i, networkImage.getNetworkWeights()[i]);
        }

        return neuralNetwork;
    }
//    
    public final int MAX_START_VALUE;
    
    protected NetworkLayer[] layers;
    protected double[] weights;
    
    public NeuralNetwork(NetworkLayer[] layers){
        if (layers == null){
            throw new IllegalArgumentException("Layers not null.");
        }
        
        if (layers.length <= 0){
            throw new IllegalArgumentException("Layers length, must be greater than zero");
        }

        MAX_START_VALUE = 1;
        
        this.layers = layers;
        
        int sumOfWeights = 0;
        for (int i=0; i<layers.length; i++){
            sumOfWeights += layers[i].getNeuronsCount() * layers[i].getLayerInputCount();//plus one for BIAS weight
        }
        
        weights = new double[sumOfWeights];
        
        int startPoint = 0;
        for (int i=0; i<layers.length; i++){
            layers[i].buildNeurons(weights, startPoint);
            startPoint += layers[i].getNeuronsCount() * layers[i].getLayerInputCount();
        }        
    }
    
    public NeuralNetwork(NetworkLayer[] layers, int maxStartValue){
        if (layers == null){
            throw new IllegalArgumentException("Layers not null.");
        }
        
        if (layers.length <= 0){
            throw new IllegalArgumentException("Layers length, must be greater than zero");
        }
        
        this.layers = layers;
        
        int sumOfWeights = 0;
        for (int i=0; i<layers.length; i++){
            sumOfWeights += layers[i].getNeuronsCount() * layers[i].getLayerInputCount();
        }
        
        weights = new double[sumOfWeights];
        
        MAX_START_VALUE = maxStartValue;
        
        int startPoint = 0;
        for (int i=0; i<layers.length; i++){
            layers[i].maxStartValue = MAX_START_VALUE;
            layers[i].buildNeurons(weights, startPoint);
            startPoint += layers[i].getNeuronsCount() * layers[i].getLayerInputCount();
        }
    }
    
//    protected NeuralNetwork(NeuralNetwork neuralNetwork){
//        int layersCount = neuralNetwork.getLayerCount();
//        layers = new NetworkLayer[layersCount];
//        
//        for (int i=0; i<layersCount; i++){
//            layers[i] = neuralNetwork.getLayerAt(i);
//        }
//        
//        int weightsCount = neuralNetwork.getWeightsCount();
//        weights = new double[weightsCount];
//        
//        for (int i=0; i<weightsCount; i++){
//            weights[i] = neuralNetwork.getWeightAt(i);
//        }
//    }
    
    @Override
    public int getWeightsCount(){
        return weights.length;
    }
    @Override
    public double getWeightAt(int position){
        return weights[position];
    }
    @Override
    public void setWeightAt(int position, double value){
        weights[position] = value;
    }
    public NetworkLayer getLayerAt(int position){
        return layers[position];
    }
    public int getLayerCount(){
        return layers.length;
    }
    
    @Override
    public double[] compute(double[] features){
        double[] results = features;
        for (NetworkLayer layer : layers) {
            results = layer.computeLayer(results);
        }
        
        return results;
    }
    
    public static Network buildRandomSizeNetwork(
            int inputFeatures, int output, MinMax neuronPerLayer, MinMax networkLength, Function hiddenLayerF, Function outputLayerF){
        
        if (neuronPerLayer.min < 2) throw new IllegalArgumentException("A network layer must have at least two neurons! (min="+neuronPerLayer.min+")");
        if (networkLength.min < 2) throw new IllegalArgumentException("A neural network must have at least two layers! min=("+networkLength.min+")");
        if (inputFeatures < 1) throw new IllegalArgumentException("Input features must be at least one inputFeatures="+inputFeatures);
        if (output < 1) throw new IllegalArgumentException("Output must be at least one output="+output);
        
        int tempNeuronsPerlLayer = neuronPerLayer.randomBetween();
        NetworkLayer[] networkLayers = new NetworkLayer[networkLength.randomBetween()];
//        networkLayers[0] = new NetworkLayer(tempNeuronsPerlLayer, inputFeatures);

        if (hiddenLayerF != null){
            networkLayers[0] = new NetworkLayer(tempNeuronsPerlLayer, inputFeatures, hiddenLayerF);
            
            for (int i=1; i<networkLayers.length -1; i++){
                networkLayers[i] = new NetworkLayer(neuronPerLayer.randomBetween(), tempNeuronsPerlLayer, hiddenLayerF);
                tempNeuronsPerlLayer = networkLayers[i].getNeuronsCount();
            }    
        }
        else{
            networkLayers[0] = new NetworkLayer(tempNeuronsPerlLayer, inputFeatures);
            
            for (int i=1; i<networkLayers.length -1; i++){
                networkLayers[i] = new NetworkLayer(neuronPerLayer.randomBetween(), tempNeuronsPerlLayer);
                tempNeuronsPerlLayer = networkLayers[i].getNeuronsCount();
            }
        }
        
        if (outputLayerF != null){
            networkLayers[networkLayers.length-1] = new NetworkLayer(output, tempNeuronsPerlLayer, outputLayerF);
        }
        else{
            networkLayers[networkLayers.length-1] = new NetworkLayer(output, tempNeuronsPerlLayer);
        }
        NeuralNetwork returnedNetwork = new NeuralNetwork(networkLayers, 1);
       
        
        System.out.println(returnedNetwork);
        return returnedNetwork;
    }
    
    public String toString(){
        String output = "";
        
        int layersCount = layers.length;
        for (int i=0; i<layersCount; i++){
            output += "Layer "+(i+1)+": neurons("+layers[i].getNeuronsCount()+")\n";
        }
        
        return output+"\n";
    }
}

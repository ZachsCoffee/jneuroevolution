/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.recurrent_neural_network;

import maths.Function;
import functions.ActivationFunction;
import networks.interfaces.TimeNetwork;

/**
 *
 * @author Zachs
 */
public class MultilayerRNN implements TimeNetwork{
    private final RNN[] RNN_LAYERS;
    private int weightsCount;
    
    public static MultilayerRNN build(int layers, int numberOfFeatures, Function hiddenFunction, Function outputFunction){
        if (layers < 2) throw new IllegalArgumentException(
            "layers must be at least 2, layers="+layers
        );
        
        RNN[] rnnLayers = new RNN[layers];
        for (int i=0; i<layers -1; i++){
            rnnLayers[i] = new RNN(numberOfFeatures, hiddenFunction, ActivationFunction.TANH.getFunction());
//            rnnLayers[i] = new RNN(numberOfFeatures, hiddenFunction, outputFunction);
        }
        rnnLayers[rnnLayers.length-1] = new RNN(numberOfFeatures, hiddenFunction, outputFunction);
        
        return new MultilayerRNN(rnnLayers, rnnLayers.length * rnnLayers[0].getTotalWeights());
    }
    
    public static MultilayerRNN buildRandom(int layers, int numberOfFeatures, Function hiddenFunction, Function outputFunction){
        if (layers < 2) throw new IllegalArgumentException(
            "layers must be at least 2, layers="+layers
        );
        
        RNN[] rnnLayers = new RNN[layers];
        for (int i=0; i<layers -1; i++){
//            rnnLayers[i] = new RNN(numberOfFeatures, hiddenFunction, outputFunction, true);
            rnnLayers[i] = new RNN(numberOfFeatures, hiddenFunction, ActivationFunction.TANH.getFunction(), true);
        }
        rnnLayers[rnnLayers.length -1] = new RNN(numberOfFeatures, hiddenFunction, outputFunction, true);
        
        return new MultilayerRNN(rnnLayers, rnnLayers.length * rnnLayers[0].getTotalWeights());
    }
    
    protected MultilayerRNN(RNN[] layers, int weightsCount){
        RNN_LAYERS = layers;
        this.weightsCount = weightsCount;
    }
    
    @Override
    public void startCompute() {
        RNN_LAYERS[0].startCompute();
//        for (RNN rnn : RNN_LAYERS) rnn.startCompute();
    }

    @Override
    public void endCompute() {}

    @Override
    public double[] compute(double[] features) {
        
        double result = RNN_LAYERS[0].compute(features)[0];
        for (int i=1; i<RNN_LAYERS.length; i++){
            RNN_LAYERS[i].setHiddenValue(result);
            result = RNN_LAYERS[i].compute(features)[0];
        }
        
        return new double[]{result};
    }

    @Override
    public double getWeightAt(int position) {
        return RNN_LAYERS[position / RNN_LAYERS[0].getTotalWeights()].getWeightAt(position % RNN_LAYERS[0].getTotalWeights());
    }

    @Override
    public void setWeightAt(int position, double weight) {
        RNN_LAYERS[position / RNN_LAYERS[0].getTotalWeights()].setWeightAt(position % RNN_LAYERS[0].getTotalWeights(), weight);
    }

    @Override
    public int getTotalWeights() {
        return weightsCount;
    }
}

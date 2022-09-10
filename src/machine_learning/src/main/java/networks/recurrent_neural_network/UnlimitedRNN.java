/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.recurrent_neural_network;

import maths.Function;

/**
 *
 * @author zachs
 */
public class UnlimitedRNN extends RNN {
    
    private double[] networkResults;
    private RNN[] rnns;

    public UnlimitedRNN(int numberOfFeatures, int numberOfOutputs, Function hiddenFunction, Function outputFunction) {
        super(numberOfFeatures, hiddenFunction, outputFunction);
        
        if (numberOfOutputs <= 0) throw new IllegalArgumentException(
                "number of outputs must be at least one"
        );
        
        networkResults = new double[numberOfOutputs];
        
        rnns = new RNN[numberOfOutputs];
        for (int i=0; i<numberOfOutputs; i++) {
            rnns[i] = new RNN(numberOfFeatures, hiddenFunction, outputFunction, true);
        }
    }

    @Override
    public void startCompute() {
        for (RNN rnn : rnns) {
            rnn.startCompute();
        }
    }

    @Override
    public double[] compute(double[] features) {
                
        for (int i=0; i<networkResults.length; i++) {
            networkResults[i] = rnns[i].simpleCompute(features);
        }
        
        return networkResults;
    }
}

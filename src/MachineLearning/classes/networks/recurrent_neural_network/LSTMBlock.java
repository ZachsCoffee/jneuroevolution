/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.recurrent_neural_network;

/**
 *
 * @author main
 */
public class LSTMBlock {
    private int startWeights, endWeights;
    private double pastCell = 0;

    public LSTMBlock(int startWeights, int endWeights) {
        if (endWeights - startWeights < 1)
            throw new IllegalArgumentException("endWeights - startWeights must be greater than zero. now is = "+(endWeights - startWeights));
        
        this.startWeights = startWeights;
        this.endWeights = endWeights;
    }
    
    /*public double compute(double[] inputs){
        //double forgetGate, inputGate, outputGate
    }*/
    
    private double sigmoid(double x){
        return 1/(1 + Math.exp(-x));
    }
    
    private double hyperbolicTan(double x){
        double e2X = Math.exp(2*x);
        return (e2X - 1)/(e2X + 1);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.representations;

/**
 *
 * @author zachs
 */
public class LayerImage {
    private int neuronsCount;
    private int weightsPerNeuron;
    private String activationFunction;

    public int getNeuronsCount() {
        return neuronsCount;
    }

    public int getWeightsPerNeuron() {
        return weightsPerNeuron;
    }

    public String getFunctionName() {
        return activationFunction;
    }
}

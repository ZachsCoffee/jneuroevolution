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
public class NetworkImage {
    private double[] networkWeights;
    private LayerImage[] layers;

    public double[] getNetworkWeights() {
        return networkWeights;
    }

    public LayerImage[] getLayers() {
        return layers;
    }
}

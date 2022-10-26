/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.interfaces;

import core.layer.TrainableLayer;

/**
 *
 * @author Zachs
 */
public interface Network extends TrainableLayer {
    double[] compute(double[] features);
    
    double getWeightAt(int index);
    void setWeightAt(int index, double weight);
    
    int getTotalWeights();
}

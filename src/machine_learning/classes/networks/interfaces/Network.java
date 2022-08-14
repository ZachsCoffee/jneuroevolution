/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.interfaces;

/**
 *
 * @author Zachs
 */
public interface Network {
    double[] compute(double[] features);
    
    double getWeightAt(int position);
    void setWeightAt(int position, double weight);
    
    int getTotalWeights();
}

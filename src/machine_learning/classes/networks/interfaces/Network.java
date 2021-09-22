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
    float[] compute(float[] features);
    
    float getWeightAt(int position);
    void setWeightAt(int position, float weight);
    
    int getWeightsCount();
}

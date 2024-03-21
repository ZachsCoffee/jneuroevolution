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
    public double[] compute(double[] features);
    
    public double getWeightAt(int position);
    public void setWeightAt(int position, double weight);
    
    public int getWeightsCount();
}

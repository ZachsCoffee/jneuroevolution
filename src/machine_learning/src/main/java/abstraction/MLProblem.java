/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abstraction;

import networks.interfaces.Network;

/**
 *
 * @author Zachs
 */
public interface MLProblem {
    public double[][] getTrainingSet();
    
    public double[][] getValidationSet();
    
    public double[][] getTestSet();
    
    public double[][] getTrainingData();
    
    public Network buildNetwork(int maxStartValue);
    
    public Network buildRandomNetwork(int maxStartValue);
}

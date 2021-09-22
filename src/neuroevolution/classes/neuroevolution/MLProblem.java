/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroevolution;

import data_manipulation.Dataset;
import networks.interfaces.Network;



/**
 *
 * @author Zachs
 */
public interface MLProblem {
    
    /**
     * Builds a network, this method must return a fixed size network every time.
     * @param maxStartValue The max start value for neurons weights
     * @return A fixed size network
     */
    Network buildNetwork(int maxStartValue);
    
    /**
     * Build a network, this method must return a random size network every time
     * @param maxStartValue The max start value for neurons weights
     * @return A random size network
     */
    Network buildRandomNetwork(int maxStartValue);
    
    /**
     * Evaluates the network for the specific dataset
     * @param network The network for evaluation
     * @param dataset The dataset for evaluation
     * @return The score of network for the specific dataset
     */
    float evaluateNetwork(Network network, Dataset dataset);
    
    Problem getProblem();
    
//    /**
//     * The dataset for training, with the target value
//     * @return A 2D array with the data. Each row is a feature, the last value for the feature MUST be the target value
//     */
//    double[][] getTrainingDataset();
//
//    /**
//     * The dataset for training, WITHOUT the target value
//     * @return A 2D array with the data. Each row is a feature.
//     */
//    double[][] getTrainingData();
}

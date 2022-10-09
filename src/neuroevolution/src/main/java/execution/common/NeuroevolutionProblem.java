/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution.common;

import data_manipulation.RawDataset;
import execution.Problem;
import networks.interfaces.Network;



/**
 *
 * @author Zachs
 */
public interface NeuroevolutionProblem<P> {
    
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
     * @param rawDataset The dataset for evaluation
     * @return The score of network for the specific dataset
     */
    double evaluateNetwork(Network network, RawDataset rawDataset);
    
    Problem<P, RawDataset> getProblem();
}

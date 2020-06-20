/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.interfaces;

/**
 * Defines a network for time series like problems
 * @author Zachs
 */
public interface TimeNetwork extends Network{
    
    /**
     * Call this method in order to let the network known when you start the computation
     */
    public void startCompute();
    
    /**
     * Call this method in order to let the network known when you stop the computation
     */
    public void endCompute();
}

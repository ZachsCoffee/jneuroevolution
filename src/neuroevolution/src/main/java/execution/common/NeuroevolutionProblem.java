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
public interface NeuroevolutionProblem<P> extends GenericProblem<P> {
    
    Problem<P, RawDataset> getProblem();
}

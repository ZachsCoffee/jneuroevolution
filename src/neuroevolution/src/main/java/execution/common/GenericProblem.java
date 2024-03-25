package execution.common;

import data_manipulation.RawDataset;
import evolution_builder.components.EvolutionPhases;
import networks.interfaces.Network;

public interface GenericProblem<P> extends EvolutionPhases<P> {
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

    NeuroevolutionPersonManager<P> getPersonManager();
}

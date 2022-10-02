package network;

import functions.ActivationFunction;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;
import networks.multilayer_perceptron.network.NeuralNetwork;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NeuralNetworkTest {

    @Test
    public void testNeuralNetwork() {
        double[] weights = new double[] {
            -1, -1, -1, 1, 1,
            1, 1, 1, 1, 1,
            1, 1, 1, 1, 1,
            -1, -1, -1, 1, 1,
            2, 2, 2, 1, 1
        };

        NeuralNetwork neuralNetwork = NeuralNetworkBuilder.initialize(3)
            .addLayer(3, ActivationFunction.GROUND_RELU.getFunction())
            .addLayer(2)
            .build(weights);

            double[] actual = neuralNetwork.compute(new double[] {1, 1, 1});

            assertArrayEquals(new double[] {-7, 17}, actual);
    }
}

package convolution;

import core.layer.TrainableLayer;
import executors.TrainableSystem;
import layers.flatten.FlatLayer;
import layers.trainable.TrainableConvolutionLayer;
import networks.multilayer_perceptron.network.NetworkLayer;
import networks.multilayer_perceptron.network.NeuralNetwork;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Random;

public class TrainableLayerTest {

    @Test
    public void testWeightIO() {
        TrainableLayer trainableLayer = new TrainableSystem(
            new ArrayList<>() {{
                add(new TrainableConvolutionLayer(1, 1, false, 1, false));
                add(new FlatLayer());
                add(new NeuralNetwork(
                    new NetworkLayer[] {
                        new NetworkLayer(3, 9),
                        new NetworkLayer(10, 3),
                    }
                ));
            }}
        );

        int count = trainableLayer.getTotalWeights();

        assertEquals(93, count);

        for (int i=1; i<count; i++) {
            assertNotEquals(trainableLayer.getWeightAt(i), trainableLayer.getWeightAt(i-1));
        }

        for (int i=0; i<count; i++) {
            double randomNumber = Math.random() * 100;

            trainableLayer.setWeightAt(i, randomNumber);

            assertEquals(randomNumber, trainableLayer.getWeightAt(i));
        }
    }
}

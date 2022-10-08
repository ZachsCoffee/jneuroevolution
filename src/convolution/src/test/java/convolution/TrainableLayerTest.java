package convolution;

import core.layer.MatrixReader;
import core.layer.TrainableLayer;
import executors.TrainableSystem;
import layers.flatten.FlatLayer;
import layers.trainable.TrainableConvolutionLayer;
import maths.matrix.Matrix2D;
import networks.multilayer_perceptron.network.NetworkLayer;
import networks.multilayer_perceptron.network.NeuralNetwork;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public class TrainableLayerTest {

    @ParameterizedTest
    @MethodSource("executeData")
    public void testExecute(
        MatrixReader[] channels,
        double[] weights,
        boolean sumKernels,
        MatrixReader[] expected
    ) {
        TrainableConvolutionLayer trainableConvolutionLayer = new TrainableConvolutionLayer(
            2,
            1,
            sumKernels,
            1,
            false
        );

        setWeights(trainableConvolutionLayer, weights);

        MatrixReader[] actual = trainableConvolutionLayer.execute(channels);

        assertEquals(expected.length, actual.length);

        for (int i = 0; i < expected.length; i++) {
            Iterator<Double> expectedIterator = expected[i].iterator();
            Iterator<Double> actualIterator = actual[i].iterator();

            for (
                Double e = expectedIterator.next(), a = actualIterator.next();
                expectedIterator.hasNext();
                e = expectedIterator.next(), a = actualIterator.next()
            ) {
                assertEquals(e, a);
            }
        }
    }

    public static Stream<Arguments> executeData() {
        return Stream.of(
            arguments(
                new MatrixReader[]{
                    new Matrix2D(
                        new double[][]{
                            {1, 1, 0, 2, 1,},
                            {1, 1, 0, 2, 1,},
                            {1, 1, 0, 2, 1,},
                            {9, 0, 6, 3, 2,},
                        }
                    ),
                    new Matrix2D(
                        new double[][]{
                            {1, 1, 0, 2, 1,},
                            {1, 1, 0, 2, 1,},
                            {1, 1, 0, 2, 1,},
                            {2, 0, 0, 3, 1,},
                        }
                    ),
                },
                new double[] {
                    1, 1, 0,
                    -1, -1, 1,
                    2, 3, 0, 2,

                    0, 2, 1,
                    0, 1, -1,
                    2, -1, 0, 1,
                },
                false,
                new MatrixReader[]{
                    new Matrix2D(
                        new double[][]{
                            {7, 6, 9},
                            {20, 22, 24},
                        }
                    ),
                    new Matrix2D(
                        new double[][]{
                            {5, 3, 5},
                            {8, 1, 4},
                        }
                    ),
                }
            ),
            arguments(
                new MatrixReader[]{
                    new Matrix2D(
                        new double[][]{
                            {1, 1, 0, 2, 1,},
                            {1, 1, 0, 2, 1,},
                            {1, 1, 0, 2, 1,},
                            {9, 0, 6, 3, 2,},
                        }
                    ),
                    new Matrix2D(
                        new double[][]{
                            {1, 1, 0, 2, 1,},
                            {1, 1, 0, 2, 1,},
                            {1, 1, 0, 2, 1,},
                            {2, 0, 0, 3, 1,},
                        }
                    ),
                },
                new double[] {
                    1, 1, 0,
                    -1, -1, 1,
                    2, 3, 0,

                    0, 2, 1,
                    0, 1, -1,
                    2, -1, 0, 1,
                },
                true,
                new MatrixReader[]{
                    new Matrix2D(
                        new double[][]{
                            {10, 7, 12},
                            {26, 21, 26},
                        }
                    ),
                }
            )
        );
    }

    private void setWeights(TrainableConvolutionLayer trainableConvolutionLayer, double[] weights) {
        for (int i=0; i<trainableConvolutionLayer.getTotalWeights(); i++) {
            trainableConvolutionLayer.setWeightAt(i, weights[i]);
        }
    }

    @Test
    public void testWeightIO() {
        TrainableLayer trainableLayer = new TrainableSystem(
            new ArrayList<>() {{
                add(new TrainableConvolutionLayer(1, 1, false, 1, false));
                add(new FlatLayer());
                add(new NeuralNetwork(
                    new NetworkLayer[]{
                        new NetworkLayer(3, 9),
                        new NetworkLayer(10, 3),
                    }
                ));
            }}
        );

        int count = trainableLayer.getTotalWeights();

        assertEquals(93, count);

        for (int i = 1; i < count; i++) {
            assertNotEquals(trainableLayer.getWeightAt(i), trainableLayer.getWeightAt(i - 1));
        }

        for (int i = 0; i < count; i++) {
            double randomNumber = Math.random() * 100;

            trainableLayer.setWeightAt(i, randomNumber);

            assertEquals(randomNumber, trainableLayer.getWeightAt(i));
        }
    }
}

package TrainableConvolution;

import common.TrainableConvolutionSystemBuilder;
import core.layer.MatrixReader;
import executors.TrainableSystem;
import maths.matrix.Matrix2D;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.params.provider.Arguments.*;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class TrainableConvolutionTest {

    @ParameterizedTest
    @MethodSource("trainableConvolution")
    public void testTrainableConvolution(
        TrainableSystem trainableSystem,
        MatrixReader[] expected
    ) {
        MatrixReader[] channels = new MatrixReader[] {
            new Matrix2D(
                new double[][] {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                }
            ),
            new Matrix2D(
                new double[][] {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                }
            )
        };

        MatrixReader[] actual = trainableSystem.execute(channels);

    }

    private static Stream<Arguments> trainableConvolution() {
        return Stream.of(
            arguments(
                TrainableConvolutionSystemBuilder.getInstance(2, 9, 9)
                    .addConvolutionLayer()
                    .and()
                    .addPoolingLayer()
                    .and()
                    .addNeuralNetworkLayer()
                    .addLayer(3)
                    .addLayer(1)
                    .and()
                    .build(),
                new MatrixReader[]{
                    new Matrix2D(
                        new double[][]{{1,1,1}}
                    )
                }
            )
        );
    }
}

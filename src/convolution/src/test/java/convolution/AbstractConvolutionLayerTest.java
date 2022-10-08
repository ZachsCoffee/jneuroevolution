package convolution;

import core.layer.Layer;
import core.layer.MatrixReader;
import core.layer.MatrixSchema;
import core.schema.SchemaRow;
import filters.Kernel;
import filters.TrainableKernel;
import functions.ActivationFunction;
import layers.convolution.AbstractConvolutionLayer;
import maths.matrix.Matrix2D;
import maths.matrix.MatrixRW;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import schema.SchemaComputer;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class AbstractConvolutionLayerTest {

    @ParameterizedTest
    @MethodSource("computeForKernelData")
    public void testComputeForKernel2(MatrixReader channel, double[] kernelWeights, double[][] expected) {
        SchemaComputer schemaComputer = new SchemaComputer(1, false);
        TrainableKernel trainableKernel = new TrainableKernel(
            0,
            3,
            ActivationFunction.GROUND_RELU.getFunction(),
            true
        );

        trainableKernel.setGlobalWeights(kernelWeights);

        StubConvolutionLayer abstractConvolutionLayer = new StubConvolutionLayer();

        MatrixRW matrixRW = abstractConvolutionLayer.computeForKernel(
            channel,
            schemaComputer,
            trainableKernel
        );

        assertEquals(matrixRW.getRowsCount(), expected.length);
        assertEquals(matrixRW.getColumnsCount(), expected[0].length);

        int i = 0;
        for (Double d : matrixRW) {
            assertEquals(d, expected[i / expected[0].length][i % expected[0].length]);
            i++;
        }
    }

    private static Stream<Arguments> computeForKernelData() {
        return Stream.of(
            arguments(
                new Matrix2D(
                    new double[][]{
                        {1, 1, - 1, - 1, - 1,},
                        {1, 2, 1, - 1, - 1,},
                        {0, 1, - 1, - 1, 1,},
                        {5, 1, 4, 6, 1,},
                    }
                ),
                new double[]{
                    1, - 1, 0,
                    - 1, 0, 2,
                    1, 1, 1, 2
                },
                new double[][] {
                    {3, 0, 0},
                    {9, 11, 18},
                }
            )
        );
    }

    private MatrixReader[] getChannels() {
        return new MatrixReader[] {
            new Matrix2D(
                new double[][] {
                    {1, 1, -1, -1, -1,},
                    {1, 2, 1, -1, -1,},
                    {0, 1, -1, -1, 1,},
                    {5, 1, 4, 6, 1,},
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
    }

    private static class StubConvolutionLayer extends AbstractConvolutionLayer{

        @Override
        public Layer copy() {
            return null;
        }

        @Override
        public MatrixReader[] execute(MatrixReader[] inputChannels) {
            return new MatrixReader[0];
        }

        @Override
        public MatrixSchema[] getSchema(MatrixSchema[] inputSchema) {
            return new MatrixSchema[0];
        }

        @Override
        public SchemaRow getSchemaRow(MatrixSchema[] inputSchema) {
            return null;
        }

        @Override
        public MatrixRW computeForKernel(MatrixReader channel, SchemaComputer schemaComputer, Kernel kernel) {
            return super.computeForKernel(channel, schemaComputer, kernel);
        }
    }
}

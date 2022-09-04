package filters;

import functions.ActivationFunction;
import maths.matrix.Matrix2D;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TrainableKernelTest {

    @Test
    public void testCompute() {
        TrainableKernel trainableKernel = new TrainableKernel(
            0,
            3,
            verify(ActivationFunction.GROUND_RELU.getFunction(), times(1)),
            true
        );

        Matrix2D matrix2D = new Matrix2D(
            new double[][] {
                {1, 4, 6, 1, 4, 6, 9},
                {1, 4, 6, 1, 4, 6, 9},
                {1, 4, 6, 1, 4, 6, 9},
            }
        );

        trainableKernel.setGlobalWeights(new double[] {
            1, 0, 1,
            0, 1, 0,
            0, 1, 1,
            2
        });

        double result = trainableKernel.compute(matrix2D, 0, 0);

        assertEquals(23, result);
    }
}

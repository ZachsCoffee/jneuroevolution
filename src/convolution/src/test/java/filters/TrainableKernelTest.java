package filters;

import maths.Function;
import maths.matrix.Matrix2D;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TrainableKernelTest {

    @Mock
    private Function function;

    @ParameterizedTest
    @MethodSource("testCompute2")
    public void testCompute(int startRowIndex, int startColumnIndex, double expected) {
        TrainableKernel trainableKernel = new TrainableKernel(
            0,
            3,
            function,
            true
        );

        when(function.compute(expected)).thenReturn(expected);

        Matrix2D matrix2D = new Matrix2D(
            new double[][]{
                {1, 4, 6, 1, 4, 6, 9},
                {1, 4, 6, 1, 4, 6, 9},
                {1, 4, 6, 1, 4, 6, 9},
            }
        );

        trainableKernel.setGlobalWeights(new double[]{
            1, 0, 1,
            0, 1, 0,
            0, 1, 1,
            2
        });

        double result = trainableKernel.compute(matrix2D, startRowIndex, startColumnIndex);

        verify(function, times(1)).compute(expected);

        assertEquals(expected, result);
    }

    private static Stream<Arguments> testCompute2() {
        return Stream.of(
            Arguments.of(0, 0, 23),
            Arguments.of(0, 2, 18),
            Arguments.of(0, 4, 36)
        );
    }
}

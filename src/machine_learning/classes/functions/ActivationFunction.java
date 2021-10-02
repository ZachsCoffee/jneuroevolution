/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functions;

import maths.Function;

/**
 *
 * @author Zachs
 */
public enum ActivationFunction {
    TANH(
            Math::tanh
    ),
    TANSIG(
            x -> 2 / (1 + Math.exp(-2 * x)) - 1
    ),
    LOGSIG(
            x -> 1 / (1 + Math.exp(-3 * x))

    ),
    SIGMOID2(
            x -> x / (1 + Math.abs(x))
    ),
    SIGMOID(
            x -> 1 / (1 + Math.exp(-x))
    ),
    GAUSS(
            x -> Math.exp(-Math.pow(x, 2))
    ),
    RELU(
            x -> Math.log(1 + Math.exp(x))
    ),
    GROUND_RELU(
            x -> Math.max(0, x)
    ),
    LEAKY_RELU(
            x -> Math.max(x * 0.1, x)
    ),
    SWISH(
            x -> x / (1 + Math.exp(-x))
    ),
    TEST(
            x -> Math.pow(Math.abs(x), 2)
    );

    private final Function function;

    ActivationFunction(Function function) {
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    public static ActivationFunction getByFunction(Function function) {

        for (ActivationFunction activationFunction : ActivationFunction.values()) {
            if (activationFunction.function.equals(function)) {
                return activationFunction;
            }
        }

        throw new FunctionNotFoundException("Failed to find activation function with object reference: "+function);
    }
}

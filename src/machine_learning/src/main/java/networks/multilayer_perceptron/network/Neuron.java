/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.multilayer_perceptron.network;

import maths.Function;

/**
 * @author main
 */
public class Neuron {
    public static final int EXTRA_WEIGHTS = 2;
    protected final int START_POINT, END_POINT, ADD_BIAS_POS, MUL_BIAS_POS;
    protected final Function function;
    protected double[] allWeights;
    private double neuronSum;

    Neuron(double[] allWeights, int startPoint, int endPoint, int maxStartValue) {
        this(allWeights, startPoint, endPoint, maxStartValue, null);
    }

    Neuron(double[] allWeights, int startPoint, int endPoint, int maxStartValue, Function function) {
        if (allWeights == null) {
            throw new IllegalArgumentException(
                    "Arguments, at pos 1: not null."
            );
        }

        END_POINT = endPoint;
        START_POINT = startPoint;
        this.allWeights = allWeights;

        ADD_BIAS_POS = endPoint - 1;
        MUL_BIAS_POS = endPoint - 2;

        for (int i = startPoint; i < endPoint; i++) {
            allWeights[i] = Math.random() * (maxStartValue * 2) - maxStartValue;
        }

        this.function = function;
    }

    public int getWeightsCount() {
        return END_POINT - START_POINT;
    }

    public int getInputsCount() {
        return END_POINT - START_POINT - EXTRA_WEIGHTS;
    }

    public double getNeuronSum() {
        return neuronSum;
    }

    public void addToWeight(double value, int position) {
        if (position >= END_POINT - START_POINT - EXTRA_WEIGHTS || position < 0) throw new RuntimeException(
                "position out of bounds. position=" + position + " length=" + (END_POINT - START_POINT)
        );

        allWeights[START_POINT + position] += value;
    }

    public void addToBias(double value) {
        allWeights[ADD_BIAS_POS] += value;
    }

    public double getWeightAt(int position) {
        if (position >= END_POINT - START_POINT - EXTRA_WEIGHTS || position < 0) throw new RuntimeException(
                "position out of bounds. position=" + position + " length=" + (END_POINT - START_POINT)
        );

        return allWeights[START_POINT + position];
    }

    public void setWeightAt(double weight, int position) {
        if (position >= END_POINT - START_POINT - EXTRA_WEIGHTS || position < 0) throw new RuntimeException(
                "position out of bounds. position=" + position + " length=" + (END_POINT - START_POINT)
        );

        allWeights[START_POINT + position] = weight;
    }

    public void setBias(double newBias) {
        allWeights[ADD_BIAS_POS] = newBias;
    }

    public double getBias() {
        return allWeights[ADD_BIAS_POS];
    }

    protected double compute(double[] features) {
        if (features.length != END_POINT - START_POINT - EXTRA_WEIGHTS) {
            throw new IllegalArgumentException(
                    "Features length is different from weights length, features=" + features.length + " weights=" + (END_POINT - START_POINT)
            );
        }

        double sum = 0;
        if (function == null) {
            for (int i = START_POINT; i < END_POINT - EXTRA_WEIGHTS; i++) {
                sum += allWeights[i] * features[i - START_POINT];
            }

            sum = sum + allWeights[ADD_BIAS_POS];
//            sum = (sum + allWeights[ADD_BIAS_POS]) * allWeights[MUL_BIAS_POS];

//          origin  sum = sum * allWeights[MUL_BIAS_POS] + allWeights[ADD_BIAS_POS];

//            sum += allWeights[ADD_BIAS_POS];
//            sum *= allWeights[MUL_BIAS_POS];

            neuronSum = sum;

            return sum;
        }
        else {
            for (int i = START_POINT; i < END_POINT - EXTRA_WEIGHTS; i++) {
                sum += allWeights[i] * features[i - START_POINT];
            }

            sum = sum + allWeights[ADD_BIAS_POS];

//            sum = (sum + allWeights[ADD_BIAS_POS]) * allWeights[MUL_BIAS_POS];
//          origin  sum = sum * allWeights[MUL_BIAS_POS] + allWeights[ADD_BIAS_POS];

//            sum += allWeights[ADD_BIAS_POS];
//            sum *= allWeights[MUL_BIAS_POS];

            neuronSum = sum;

            return function.compute(sum);
        }
    }
}

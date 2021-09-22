/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.multilayer_perceptron;

import maths.Function;

/**
 * @author main
 */
public class Neuron {
    public static final int EXTRA_WEIGHTS = 1;
    protected final int START_POINT, END_POINT, ADD_BIAS_POS, MUL_BIAS_POS;
    protected final Function function;
    protected float[] allWeights;
    private double neuronSum;

    public Neuron(float[] allWeights, int startPoint, int endPoint, int maxStartValue) {
        this(allWeights, startPoint, endPoint, maxStartValue, null);
    }

    public Neuron(float[] allWeights, int startPoint, int endPoint, int maxStartValue, Function function) {
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
        int j = 0;
        for (int i = startPoint; i < endPoint; i++) {
            allWeights[i] = 1;//(float) (Math.random() * (maxStartValue * 2) - maxStartValue);
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

    protected void setWeightAt(float weight, int position) {
        if (position >= END_POINT - START_POINT - EXTRA_WEIGHTS || position < 0) throw new RuntimeException(
                "position out of bounds. position=" + position + " length=" + (END_POINT - START_POINT)
        );

        allWeights[START_POINT + position] = weight;
    }

    protected double getWeightAt(int position) {
        if (position >= END_POINT - START_POINT - EXTRA_WEIGHTS || position < 0) throw new RuntimeException(
                "position out of bounds. position=" + position + " length=" + (END_POINT - START_POINT)
        );

        return allWeights[START_POINT + position];
    }

    protected void addToWeight(float value, int position) {
        if (position >= END_POINT - START_POINT - EXTRA_WEIGHTS || position < 0) throw new RuntimeException(
                "position out of bounds. position=" + position + " length=" + (END_POINT - START_POINT)
        );

        allWeights[START_POINT + position] += value;
    }

    protected void setBias(float newBias) {
        allWeights[ADD_BIAS_POS] = newBias;
    }

    protected double getBias() {
        return allWeights[ADD_BIAS_POS];
    }

    protected void addToBias(float value) {
        allWeights[ADD_BIAS_POS] += value;
    }

    protected float compute(float[] features) {
        if (features.length != END_POINT - START_POINT - EXTRA_WEIGHTS) {
            throw new IllegalArgumentException(
                    "Features length is different from weights length, features=" + features.length + " weights=" + (END_POINT - START_POINT)
            );
        }

        float sum = 0;
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

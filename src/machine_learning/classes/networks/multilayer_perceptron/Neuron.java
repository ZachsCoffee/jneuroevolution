/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.multilayer_perceptron;

import maths.Function;

/**
 *
 * @author main
 */
public class Neuron {
    public static final int EXTRA_WEIGHTS = 2;
    
    private double neuronSum;
    
    protected final int START_POINT, END_POINT, ADD_BIAS_POS, MUL_BIAS_POS;
    
    protected Function function;
    protected double[] allWeights;
    
    public Neuron(double[] allWeights, int startPoint, int endPoint, int maxStartValue){
        if (allWeights == null){
            throw new IllegalArgumentException(
                    "Arguments, at pos 1: not null."
            );
        }
        
        END_POINT = endPoint;
        START_POINT = startPoint;
        this.allWeights = allWeights;
        
        ADD_BIAS_POS = endPoint -1;
        MUL_BIAS_POS = endPoint -2;
        
        for (int i=startPoint; i<endPoint; i++){
            allWeights[i] = Math.random()*(maxStartValue*2)-maxStartValue;
        }
        
    }
    
    public Neuron(double[] allWeights, int startPoint, int endPoint, int maxStartValue, Function function){
        this(allWeights, startPoint, endPoint, maxStartValue);
        
        if (function == null){
            throw new IllegalArgumentException(
                    "Argument, at pos 2: not null."
            );
        }
        
        this.function = function;
    }
    
    public int getWeightsCount(){
        return END_POINT - START_POINT;
    }
    public int getInputsCount(){
        return END_POINT - START_POINT -EXTRA_WEIGHTS;
    }
    public double getNeuronSum(){
        return neuronSum;
    }
    protected void setWeightAt(double weight, int position){
        if (position >= END_POINT - START_POINT -EXTRA_WEIGHTS || position < 0) throw new RuntimeException(
                "position out of bounds. position="+position+" length="+(END_POINT - START_POINT)
        );
        
        allWeights[START_POINT + position] = weight;
    }
    
    protected double getWeightAt(int position){
        if (position >= END_POINT - START_POINT -EXTRA_WEIGHTS || position < 0) throw new RuntimeException(
                "position out of bounds. position="+position+" length="+(END_POINT - START_POINT)
        );
        
        return allWeights[START_POINT + position];
    }
    
    protected void addToWeight(double value, int position){
        if (position >= END_POINT - START_POINT -EXTRA_WEIGHTS || position < 0) throw new RuntimeException(
                "position out of bounds. position="+position+" length="+(END_POINT - START_POINT)
        );
        
        allWeights[START_POINT + position] += value;
    }
    
    protected void setBias(double newBias){
        allWeights[ADD_BIAS_POS] = newBias;
    }
    
    protected double getBias(){
        return allWeights[ADD_BIAS_POS];
    }
    
    protected void addToBias(double value){
        allWeights[ADD_BIAS_POS] += value;
    }
    
    protected double compute(double[] features){
        if (features.length != END_POINT - START_POINT -EXTRA_WEIGHTS){
            throw new IllegalArgumentException(
                    "Features length is different from weights length, features="+features.length+" weights="+(END_POINT - START_POINT)
            );
        }

        if (function == null){
            double sum = 0;
            for (int i=START_POINT; i<END_POINT -EXTRA_WEIGHTS; i++){
                sum += allWeights[i] * features[i - START_POINT];
            }
            
            sum = sum * allWeights[MUL_BIAS_POS] + allWeights[ADD_BIAS_POS];
//            sum += allWeights[ADD_BIAS_POS];
//            sum *= allWeights[MUL_BIAS_POS];
            
            neuronSum = sum;
            
            return sum;
        }
        else{
            double sum = 0;
            for (int i=START_POINT; i<END_POINT -EXTRA_WEIGHTS; i++){
                sum += allWeights[i] * features[i - START_POINT];
            }
            
            sum = sum * allWeights[MUL_BIAS_POS] + allWeights[ADD_BIAS_POS];
//            sum += allWeights[ADD_BIAS_POS];
//            sum *= allWeights[MUL_BIAS_POS];
            
            neuronSum = sum;
            
            return function.compute(sum);
        }
    }
}

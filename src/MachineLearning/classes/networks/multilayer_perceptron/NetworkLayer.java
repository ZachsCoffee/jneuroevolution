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
public class NetworkLayer{
    int maxStartValue = 1;
    
    protected int numberOfWeights;
    protected Function function;
    protected Neuron[] neuros;
    
    public NetworkLayer(int numberOfNeurons, int numberOfWeights){
        
        if (numberOfNeurons <= 0 || numberOfWeights <=0){
            throw new IllegalArgumentException("Number of nodes and number of weights must be greater than zero.");
        }
        
        this.numberOfWeights = numberOfWeights + Neuron.EXTRA_WEIGHTS;// the weights for one neuron
        neuros = new Neuron[numberOfNeurons];
    }
    
    public NetworkLayer(int numberOfNeurons, int numberOfWeights, Function function){
        this(numberOfNeurons, numberOfWeights);
        
        if (function == null){
            throw new IllegalArgumentException("Function not null.");
        }
        
        this.function = function;
    }
    
    public Neuron getNeuronAt(int position){
        return neuros[position];
    }
    public int getNeuronsCount(){
        return neuros.length;
    }
    public int getLayerInputCount(){
        return numberOfWeights;
    }
    public Function getFunction(){
        return function;
    }
    
    protected void setNeuronAt(int position, Neuron neuron){
        neuros[position] = neuron;
    }
    
    protected void buildNeurons(double[] weights, int startPoint){
        int endPoint = startPoint + numberOfWeights;
        for (int i=0; i<neuros.length; i++){
            if (function == null){
                neuros[i] = new Neuron(weights, startPoint, endPoint, maxStartValue);  
            }
            else{
                neuros[i] = new Neuron(weights, startPoint, endPoint, maxStartValue, function);
            }
            
            startPoint += numberOfWeights;
            endPoint += numberOfWeights;
        }
    }
    
    public double runFuntion(double x){
        return function.compute(x);
    }
    
    protected double[] computeLayer(double[] layerInputs){
        /*if (features.length != neuros.length){
            throw new IllegalArgumentException("Features leangth is different from neurons lenght. features="+features.length+" neurons="+neuros.length);
        }*/
        
        double[] results = new double[neuros.length];
        for (int i=0; i<neuros.length; i++){
            results[i] = neuros[i].compute(layerInputs);
        }
        
        return results;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks.recurrent_neural_network;

import java.util.Random;
import maths.Function;
import networks.interfaces.TimeNetwork;

/**
 *
 * @author Zachs
 */
public class RNN implements TimeNetwork{
    
    private Function hiddenFunction, outputFunction;
    
    private double[][] allArrays;
    private double[] wVector, uVector, vVector;
    private double hiddenValue = 0;
    
    private int numberOfFeatures;
    private int weightsCount;
    
    public static RNN buildRandom(int numberOfFeatures, Function hiddenFunction, Function outputFunction){
        return new RNN(numberOfFeatures, hiddenFunction, outputFunction, true);
    }
    public static RNN build(int numberOfFeatures, Function hiddenFunction, Function outputFunction){
        return new RNN(numberOfFeatures, hiddenFunction, outputFunction);
    }
    
    protected RNN(int numberOfFeatures, Function hiddenFunction, Function outputFunction){
        if (hiddenFunction == null) throw new IllegalArgumentException("hiddenFunction not null");
        if (outputFunction == null) throw new IllegalArgumentException("outputFunction not null");
        if (numberOfFeatures < 1) throw new IllegalArgumentException("numberOfFeatures must be greater or equal to one, numberOfFeatures="+numberOfFeatures);
        
        this.hiddenFunction = hiddenFunction;
        this.outputFunction = outputFunction;
        this.numberOfFeatures = numberOfFeatures;
        
        wVector = new double[numberOfFeatures];
        uVector = new double[numberOfFeatures];
        vVector = new double[numberOfFeatures];
        
        allArrays = new double[][]{
            wVector, uVector, vVector
        };
        
        weightsCount = allArrays.length * numberOfFeatures;
    }
    protected RNN(int numberOfFeatures, Function hiddenFunction, Function outputFunction, boolean isRandom){
        if (hiddenFunction == null) throw new IllegalArgumentException("hiddenFunction not null");
        if (outputFunction == null) throw new IllegalArgumentException("outputFunction not null");
        if (numberOfFeatures < 1) throw new IllegalArgumentException("numberOfFeatures must be greater or equal to one, numberOfFeatures="+numberOfFeatures);
        
        this.hiddenFunction = hiddenFunction;
        this.outputFunction = outputFunction;
        this.numberOfFeatures = numberOfFeatures;
        
        Random randomGeneretor = new Random();
        wVector = randomGeneretor.doubles(numberOfFeatures, -1, 1).toArray();
        uVector = randomGeneretor.doubles(numberOfFeatures, -1, 1).toArray();
        vVector = randomGeneretor.doubles(numberOfFeatures, -1, 1).toArray();
        
        allArrays = new double[][]{
            wVector, uVector, vVector
        };
        
        weightsCount = allArrays.length * numberOfFeatures;
    }
    
    void setHiddenValue(double hiddenValue) {
        this.hiddenValue = hiddenValue;
    }
    
    @Override
    public double getWeightAt(int position) {
        return allArrays[position / numberOfFeatures][position % numberOfFeatures];
    }
    @Override
    public int getTotalWeights() {
        return weightsCount;
    }
    @Override
    public void setWeightAt(int position, double weight) {
        allArrays[position / numberOfFeatures][position % numberOfFeatures] = weight;
    }
    
    @Override
    public double[] compute(double[] features){
        double sum = 0;
        
        for (int i=0; i<numberOfFeatures; i++){
            sum += hiddenValue * wVector[i] + uVector[i]*features[i];
        }
        
        hiddenValue = hiddenFunction.compute(sum);// + Functions.tansig().compute(sum));
        
        sum = 0;
        for (int i=0; i<features.length; i++){
            sum += hiddenValue * vVector[i];
        }

        return new double[]{outputFunction.compute(sum)};// + Functions.groundRelu().compute(sum))};
    }

    protected double simpleCompute(double[] features) {
        double sum = 0;
        
        for (int i=0; i<numberOfFeatures; i++){
            sum += hiddenValue * wVector[i] + uVector[i]*features[i];
        }
        
        hiddenValue = hiddenFunction.compute(sum);
        
        sum = 0;
        for (int i=0; i<features.length; i++){
            sum += hiddenValue * vVector[i];
        }

        return outputFunction.compute(sum);
    }
    
    @Override
    public void startCompute() {
        hiddenValue = 1;
    }

    @Override
    public void endCompute() {
        
    }
}
//}
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package machine_learning.rnn;
//
//import java.util.Random;
//import machine_learning.Function;
//import machine_learning.Functions;
//import machine_learning.Network;
//import core.matrix.Matrix;
//
///**
// *
// * @author Zachs
// */
//public class RNN implements Network{
//    
//    private Function hiddenFunction, outputFunction;
//    
//    private double[][] allArrays;
//    private double[] wVector, uVector, vVector, hVector;
//    
//    private int numberOfFeatures;
//    private int weightsCount;
//    private int networkSize;
//    
//    public RNN(int networkSize, int numberOfFeatures, Function hiddenFunction, Function outputFunction){
//        if (networkSize < 1) throw new IllegalArgumentException("network size must be at least 1 networkSize="+networkSize);
//        if (hiddenFunction == null) throw new IllegalArgumentException("hiddenFunction not null");
//        if (outputFunction == null) throw new IllegalArgumentException("outputFunction not null");
//        if (numberOfFeatures < 1) throw new IllegalArgumentException("numberOfFeatures must be greater or equal to one, numberOfFeatures="+numberOfFeatures);
//        
//        this.hiddenFunction = hiddenFunction;
//        this.outputFunction = outputFunction;
//        this.numberOfFeatures = numberOfFeatures;
//        this.networkSize = networkSize;
//        
//        wVector = new double[numberOfFeatures][networkSize];
//        uVector = new double[numberOfFeatures][networkSize];
//        vVector = new double[numberOfFeatures][networkSize];
//        hVector = new double[numberOfFeatures][networkSize];
//        
//        allArrays = new double[][][]{
//            wVector, uVector, vVector, hVector
//        };
//        
//        weightsCount = allArrays.length * numberOfFeatures * networkSize;
//    }
//    
//    @Override
//    public double getWeightAt(int position){
//        return allArrays[position / allArrays.length][position % allArrays.length / numberOfFeatures][position % networkSize];
////        return allArrays[position / allArrays.length][position % numberOfFeatures];
//    }
//    
////    @Override
////    public double[] compute(double[] features){
////        double sum = 0;
////        
////        for (int i=0; i<features.length; i++){
////            sum += hiddenValue * wVector[i] + uVector[i]*features[i];
////        }
////        
////        hiddenValue = hiddenFunction.compute(sum + Functions.tansig().compute(sum));
////        
////        sum = 0;
////        for (int i=0; i<features.length; i++){
////            sum += hiddenValue * vVector[i];
////        }
////
////        return new double[]{outputFunction.compute(sum + Functions.groundRelu().compute(sum))};
////    }
//    @Override
//    public double[] compute(double[] features){
//        hVector = Matrix.compute(
//                Matrix.sum(
//                        Matrix.product(wVector, hVector), 
//                        Matrix.product(uVector, new double[][]{features})
//                )
//                , hiddenFunction
//        );
//        
//        return Matrix.compute(Matrix.product(uVector, hVector), outputFunction);
////        double sum = 0;
////
////        for (int i=0; i<features.length; i++){
////            hVector[i] = hiddenFunction.compute(hVector[i] * wVector[i] + uVector[i]*features[i]);
////            sum += hVector[i] * vVector[i];
////        }
////
////        return new double[]{outputFunction.compute(sum)};
//    }
//    @Override
//    public int getWeightsCount() {
//        return weightsCount;
//    }
//
//    @Override
//    public void setWeightAt(int position, double weight) {
//        allArrays[position / allArrays.length][position % allArrays.length / numberOfFeatures][position % networkSize] = weight;
////        allArrays[position / allArrays.length][position % numberOfFeatures] = weight;
//    }
//}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package learning_rate;

/**
 *
 * @author Zachs
 */
public class Adams {
    private final double B1 = 0.9, B2 = 0.999, E = 0.000000001;//fixed math staff
    private final double  LEARNING_RATE;
    private final int BATCH_SIZE;
    
    private int weightUpdatesCount = 0, biasUpdatesCount = 0;
    private double vDW = 0, vDB = 0, sDW = 0, sDB = 0;//more math staff
    
    public Adams(double learningRate, int batchSize){
        LEARNING_RATE = learningRate;
        BATCH_SIZE = batchSize;
    }
    
    public double updateWeight(double weight, double derivative){
        if (weightUpdatesCount++ > BATCH_SIZE){
            vDW = sDW = 0;
            weightUpdatesCount = 0;
        }
        
        vDW = B1 * vDW + (1 - B1) * derivative;
        sDW = B2 * sDW + (1 - B2) * Math.pow(derivative, 2);
        
        double 
                vCorrected = vDW / (1 - Math.pow(B1, weightUpdatesCount)),
                sCorrected = sDW / (1 - Math.pow(B2, weightUpdatesCount));
        
        
        return weight - LEARNING_RATE * (vCorrected / Math.sqrt(sCorrected + E));
    }
    
    public double updateBias(double bias, double derivative){
        if (biasUpdatesCount++ > BATCH_SIZE){
            vDB = sDB = 0;
            biasUpdatesCount = 0;
        }
        
        vDB = B1 * vDB + (1 - B1) * derivative;
        sDB = B2 * sDB + (1 - B2) * Math.pow(derivative, 2);
        
        double 
                vCorrected = vDB / (1 - Math.pow(B1, biasUpdatesCount)),
                sCorrected = sDB / (1 - Math.pow(B2, biasUpdatesCount));
        
        
        return bias - LEARNING_RATE * (vCorrected / Math.sqrt(sCorrected + E));
    }
    
    public void reset(){
        vDW = vDB = sDW = sDB = 0;
    }
}

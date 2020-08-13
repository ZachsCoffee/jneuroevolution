/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maths;

/**
 *
 * @author Zachs
 */
public class LinearValues {
    public static enum Order {
        ACS, DESC
    }

    private int epochs;
    private MinMax minMaxValues;
    private boolean isAsc = true;
            
    public LinearValues(MinMax mutationValues, int epochs, Order order) {
        if (mutationValues == null) throw new IllegalArgumentException(
                "mutationValues not null!"
        );
        if (epochs <= 0) throw new IllegalArgumentException(
                "Epochs must be grater than zero"
        );
        
        this.epochs = epochs;
        this.minMaxValues = mutationValues;
        
        if (order == null) throw new IllegalArgumentException(
                "order not null!"
        );
        
        isAsc = order == Order.ACS;
    }
    
    public int getEpochs() {
        return epochs;
    }

    public MinMax getMinMax() {
        return minMaxValues;
    }
    
    public int compute(int currentEpoch){
        if (isAsc){
            return ((minMaxValues.max - minMaxValues.min)*currentEpoch + epochs*minMaxValues.min) / epochs;
        }
        else{
            return ((minMaxValues.min - minMaxValues.max)*currentEpoch + epochs*minMaxValues.max) / epochs;
        }
    }
    
    public double computeD(int currentEpoch){
        if (isAsc){
            return ((minMaxValues.dmax - minMaxValues.dmin)*currentEpoch + epochs*minMaxValues.dmin) / epochs;
        }
        else{
            return ((minMaxValues.dmin - minMaxValues.dmax)*currentEpoch + epochs*minMaxValues.dmax) / epochs;
        }
//        return (minMaxValues.dmin - minMaxValues.dmax)*currentEpoch / (epochs + minMaxValues.dmax);
    }
}

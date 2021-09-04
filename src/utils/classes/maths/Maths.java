/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maths;

/**
 *
 * @author arx-dev-3a-19
 */
public final class Maths {
    private Maths(){}
    
    public static double distance(double x, double y) {
        return Math.abs(x - y);
    }
    
    public static int distance(int x, int y) {
        return Math.abs(x - y);
    }
    
    /**
     * Computes the percent based a min and max value.
     * @param value The value between the min and max values
     * @param minValue The min value
     * @param maxValue The max value
     * @return The percent value
     */
    public static double percent(double value, double minValue, double maxValue){
        return 100 * (value - minValue) / (maxValue - minValue);
    }
    
    /**
     * Computes the percent based the range value. e.g When range is set at 200 max value this function can return is 200%
     * @param value The value between the min and max values
     * @param minValue The min value
     * @param maxValue The max value
     * @param range The range e.g When range is set at 200, the max value this function can return is 200%
     * @return The percent value based the range
     */
    public static double percent(double value, double minValue, double maxValue, double range){
        return range * (value - minValue) / (maxValue - minValue);
    }
    
    /**
     * Computes the percent for a current value based on a maxValue.
     * @param maxValue The max value
     * @param currentValue The current value
     * @return The percent [0, 100]
     */
    public static double percent(double maxValue, double currentValue) {
        return 100 * currentValue / maxValue;
    }
    
    /**
     * Scales the argument value from range [minValue, maxValue] to range [scaleMin, scaleMax].
     * @param value The value in range [minValue, maxValue]
     * @param minValue The minimum value that argument "value" can take. 
     * @param maxValue The maximum value that argument "value" can take. 
     * @param scaleMin The minimum value of the new scale.
     * @param scaleMax The maximum value of the new scale.
     * @return The new value inside the range [scaleMin, scaleMax].
     */
    public static double scale(double value, double minValue, double maxValue, double scaleMin, double scaleMax) {
        if (minValue >= maxValue) throw new IllegalArgumentException(
                "minValue must be smaller than maxValue. minValue:"+minValue+", maxValue:"+maxValue
        );
        if (scaleMin >= scaleMax) throw new IllegalArgumentException(
                "scaleMin must be smaller than scaleMax. scaleMin:"+scaleMin+", scaleMax:"+scaleMax
        );
        
        
        double distance = Math.abs(scaleMax - scaleMin);
                
        return distance * (value - minValue) / (maxValue - minValue) + scaleMin;
    }
    
}

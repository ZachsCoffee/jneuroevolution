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
     * @param range The range e.g When range is set at 200 max value this function can return is 200%
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
}

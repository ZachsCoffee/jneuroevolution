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
public class MinMax {
    public int min, max;
    public double dmin, dmax;
    
    public MinMax(int min, int max){
        if (min > max) throw new RuntimeException(
                "min must be lower than max. Error: min="+min+", max="+max
        );
        
        this.min = min;
        this.max = max;
    }
    public MinMax(double dmin, double dmax){
        if (dmin > dmax) throw new RuntimeException(
                "dmin must be lower than dmax. Error: dmin="+dmin+", dmax="+dmax
        );
        
        this.dmin = dmin;
        this.dmax = dmax;
    }
    
    public int randomBetween(){
        return (int)(Math.random() * (max - min +1) + min);
    }
    public double randomBetweenD(){
        return (Math.random() * (dmax - dmin) + dmin) +1;
    }
    
    public boolean isEqual(){
        return min == max;
    }
    public boolean isEqualD(){
        return dmin == dmax;
    }
}

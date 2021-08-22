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
public class SquareFunction {
    private final double START, END;
    
    public SquareFunction(double start, double end){
        START = start;
        END = end;
    }
    
    public double compute(double x){
        return START * Math.exp(-x/END);
    }
    
}

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
public class Matrix {
    private Matrix(){}
    
    public static double[][] product(double[][] x, double[][] y){
        if (x[0].length != y.length) throw new IllegalArgumentException(
                "The columns length of x must be equal to rows length of y. Xcols="+x[0].length+" Yrows"+y.length
        );
        if (!isMatrix(x) || !isMatrix(y)) throw new IllegalArgumentException(
                "The x and y must be matrices"
        );
        
        double[][] result = new double[x.length][y[0].length];
        for (int i=0; i<x.length; i++){
            for (int j=0; j<y[0].length; j++){
                result[i][j] = dot(x[i], y, j);
            }
        }
        
        return result;
    }
    private static double dot(double[] x, double[][] y, int column){
        
        double result = 0;
        for (int i=0; i<x.length; i++){
            result += x[i] * y[i][column];
        }
        
        return result;
    }
    
    public static double[][] sum(double[][] x, double[][] y){
        if (x.length != y.length) throw new IllegalArgumentException("x and y must be two equal size matrices");

        double[][] result = new double[x.length][x[0].length];
        for (int i=0; i<result.length; i++){
            if (x[i].length != y[i].length) throw new IllegalArgumentException("x and y must be two equal size matrices");

            for (int j=0; j<result[i].length; j++){
                result[i][j] = x[i][j] + y[i][j];
            }
        }
        
        return result;
    }
    
    public static double dot(double[] x, double[] y){
        if (x.length != y.length) throw new IllegalArgumentException(
                "The x and y arrays must have the same length, Xlength="+x.length+" Ylength="+y.length
        );
        
        double result = 0;
        for (int i=0; i<x.length; i++){
            result += x[i] * y[i];
        }
        
        return result;
    }
    
    public static double[][] compute(double[][] x, Function f){
        for (int i=0; i<x.length; i++){
            for (int j=0; j<x[i].length; j++){
                x[i][j] = f.compute(x[i][j]);
            }
        }
        
        return x;
    }
    
    public static boolean isMatrix(double[][] x){
        int firstRowLength = x[0].length;
        
        for (int i=1; i<x.length; i++) if (firstRowLength != x[i].length) return false;
            
        return true;
    }
}

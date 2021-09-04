/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import maths.Function;

/**
 *
 * @author Zachs
 */
public class ActivationFunctions {
    private ActivationFunctions(){}
    
    public static Function getFunction(String functionName) {
        
        try {
            functions.ActivationFunction functionAnotation;
            for (Method method : ActivationFunctions.class.getMethods()) {
                functionAnotation = method.getAnnotation(functions.ActivationFunction.class);

                if (functionAnotation != null && functionName.equals(functionAnotation.value())) return (Function) method.invoke(null);
            }            
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(ActivationFunctions.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

       
        throw new FunctionNotFoundException("Function with name "+functionName+" not found");
    }
    
    /**
     * Hyperbolic tangent
     * @return Math.tanh(x)
     */
    @ActivationFunction("tanh")
    public static Function tanh(){
        return x -> {
            return Math.tanh(x);
        };
    }
    
    /**
     * 
     * @return 2/(1+Math.exp(-2*x)) - 1
     */
    @ActivationFunction("tansig")
    public static Function tansig(){
        return (x) -> { 
            return 2/(1+Math.exp(-2*x)) - 1;
        };
    }
    
    /**
     * 
     * @return 1/(1 + Math.exp(-3*x))
     */
    @ActivationFunction("logsig")
    public static Function logsig(){
        return (x) -> {
            return 1/(1 + Math.exp(-3*x));
        };
    } 
    
    /**
     * 
     * @return x/(1 + Math.abs(x))
     */
    @ActivationFunction("sigmoid2")
    public static Function sigmoid2(){
        return (x) -> {
            return x/(1 + Math.abs(x));
        };
    }
    
    /**
     * Logistic sigmoid 
     * @return 1 / (1 + Math.exp(-x))
     */
    @ActivationFunction("sigmoid")
    public static Function sigmoid(){
        return (x) -> {
            return 1 / (1 + Math.exp(-x));
        };
    }
    
    /**
     * 
     * @return Math.exp(-Math.pow(x, 2))
     */
    @ActivationFunction("gauss")
    public static Function gauss(){
        return (x) -> {
            return Math.exp(-Math.pow(x, 2));
        };
    }
    
    /**
     * For output neurons. Non linear. Only positive values
     * @return Math.log(1 + Math.exp(x))
     */
    @ActivationFunction("relu")
    public static Function relu(){
        return (x) -> {
            return Math.log(1 + Math.exp(x));
        };
    }
    
    /**
     * For output neurons. Linear. Only positive values
     * @return Math.max(0, x);
     */
    @ActivationFunction("groundRelu")
    public static Function groundRelu(){
        return (x) -> {
            return Math.max(0, x);
        };
    }
    
    /**
     * For output neurons. Produce positive and negative values
     * @return Math.max(x * 0.1, x)
     */
    @ActivationFunction("leakyRelu")
    public static Function leakyRelu(){
        return (x) -> {
            return Math.max(x * 0.1, x);
        };
    }
    
    @ActivationFunction("swish")
    public static Function swish(){
        return (x) -> {
            return x / (1 + Math.exp(-x));
        };
    }
    
    @ActivationFunction("test")
    public static Function test(){
        return (x) -> {
            return Math.pow(Math.abs(x), 2);
        };
    }
}

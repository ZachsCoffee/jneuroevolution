/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functions;

/**
 *
 * @author zachs
 */
public class FunctionNotFoundException extends RuntimeException {
    public FunctionNotFoundException(String message) {
        super(message);
    }
}

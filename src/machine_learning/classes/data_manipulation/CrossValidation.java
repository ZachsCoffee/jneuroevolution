/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_manipulation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author main
 */
public class CrossValidation {
    private CrossValidation(){}
    
    public static int[][] crossValidation(int trainingSize, int totalSize){
        if (totalSize <=0 || trainingSize <=0){
            throw new IllegalArgumentException("All arguments must be greater zero.");
        }
        if (totalSize < trainingSize){
            throw new IllegalArgumentException("Argument, at pos: 2 must be grater than argument at pos 1");
        }
        
        int[] training , testing;
        int[] total = new int[totalSize];
        
        for (int i=0; i<totalSize; i++){
            total[i] = i;
        }
        randomizeTable(total);
        
        training = Arrays.copyOf(total, trainingSize);

        testing = Arrays.copyOfRange(total, trainingSize, totalSize);
        //System.arraycopy(total, trainingSize, testing, trainingSize, totalSize - trainingSize);
        
        return new int[][] {training, testing};
    }
    
    private static void randomizeTable(int[] table){
        int temp, pos1, pos2;
        for (int i=1; i<=table.length; i++){
            pos1 = (int)(Math.random()*table.length);
            pos2 = (int)(Math.random()*table.length);
            
            temp = table[pos1];
            table[pos1] = table[pos2];
            table[pos2] = temp;
        }
    }
}

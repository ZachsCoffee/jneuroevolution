/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_manipulation;

import java.util.Arrays;

/**
 *
 * @author arx-dev-3a-19
 */
public class Dataset {

    public final int SIZE;
    
    public double[][] features, targets;
    
    public Dataset(double[][] features, double[][] targets) {

        if (features.length != targets.length) throw new IllegalArgumentException(
                "features and targets must have equal size"
        );
        
        SIZE = features.length;
        
        this.features = features;
        this.targets = targets;
    }
    
    public static Dataset create(double[][] rawData, int splitPoint) {
        
        Dataset dataset = new Dataset(new double[rawData.length][], new double[rawData.length][]);
        
        for (int i=0; i<rawData.length; i++) {
            
            dataset.features[i] = Arrays.copyOf(rawData[i], splitPoint);
            dataset.targets[i] = Arrays.copyOfRange(rawData[i], splitPoint, rawData[i].length);
        }
        
        return dataset;
    }
        
    public static Dataset create(double[][] rawData, int splitPoint, DataParser dataParser) {
        
        Dataset dataset = new Dataset(new double[rawData.length][], new double[rawData.length][]);
        
        for (int i=0; i<rawData.length; i++) {
            
            dataset.features[i] = Arrays.copyOf(rawData[i], splitPoint);
            dataset.targets[i] = Arrays.copyOfRange(rawData[i], splitPoint, rawData[i].length);
            
            dataParser.parse(dataset.features[i]);
            dataParser.parse(dataset.targets[i]);
        }
        
        return dataset;
    }
    /**
     * Returns a new array with the last value of each array from the given data-set.
     * @param dataset The data set
     * @return The new array
     * @throws RuntimeException If not all the arrays length isn't the same
     */
    public static double[] sliceLast(double[][] dataset){
        
        double[] slice = new double[dataset.length];
        int lastPosition = dataset[0].length -1;
        for (int i=0; i<slice.length; i++){
            
            if (dataset[i].length -1 != lastPosition) throw new RuntimeException(
                    "All arrays must have the same length. Error: row="+i+", length="+dataset[i].length
            );
            
            slice[i] = dataset[i][lastPosition];
        }
        
        return slice;
    }
    
    /**
     * Returns a new array based the data-set, with the sliced columns, from the startPosition until the end. No checks if the arrays lengths isn't the same
     * @param dataset The data-set
     * @param startPosition The position to start the slice (Include this position). Starts from 0 (zero)
     * @return The new sliced array
     */
    public static double[][] sliceColumns(double[][] dataset, int startPosition) {
        return sliceColumns(dataset, startPosition, dataset[0].length);
    }
    
    /**
     * Returns a new array based the data-set, with the sliced columns, from the startPosition until the endPosition (exclude the endPosition). No checks if the arrays lengths isn't the same
     * @param dataset The data-set
     * @param startPosition The position to start the slice (Include this position). Starts from 0 (zero)
     * @param endPosition The position to stop the slice. (Exclude this position)
     * @return The new sliced array
     */
    public static double[][] sliceColumns(double[][] dataset, int startPosition, int endPosition) {
        if (startPosition > endPosition) throw new RuntimeException(
                "Start position must be lower than endPosition. Error: startPosition="+startPosition+", endPosition="+endPosition
        );
        
        double[][] slice = new double[dataset.length][];
        
        for (int i=0; i<dataset.length; i++){
            slice[i] = Arrays.copyOfRange(dataset[i], startPosition, endPosition);
        }
        
        return slice;
    }
    
    public interface DataParser {
        public void parse(double[] dataRow);
    }
}

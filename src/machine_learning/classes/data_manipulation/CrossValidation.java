/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_manipulation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import maths.ArrayUtils;

/**
 *
 * @author main
 */
public class CrossValidation {

    public static ProblemDatasets[] kFolds(double[][] data, int foldsNumber, double validationPercent) {
        if (data == null) throw new IllegalArgumentException(
                "Data not null"
        );
        if (foldsNumber < 2) throw new IllegalArgumentException(
                "Folds number need to be at least two (2). Foldsnumber: "+foldsNumber
        );
        if (validationPercent < 0 || validationPercent > 1) throw new IllegalArgumentException(
                "Validation percent range must be [0, 1]"
        );
        
        double[][][] folds = new double[foldsNumber][][];
        
        int dataPerFold = data.length / foldsNumber;
        int extraData = data.length % foldsNumber;
        int startCopyPos = 0;
        
        int tempCopySize;
        double[][] tempCopyHolder;
        for (int i=0; i<folds.length; i++) {
            if (i < extraData) {
                tempCopySize = dataPerFold + 1;
            }
            else {
                tempCopySize = dataPerFold;
            }
            
            tempCopyHolder = new double[tempCopySize][];

            System.arraycopy(data, startCopyPos, tempCopyHolder, 0, tempCopySize);

            folds[i] = tempCopyHolder;

            startCopyPos += tempCopySize;
        }
        
        ProblemDatasets[] problemDatasets = new ProblemDatasets[foldsNumber];
        ArrayList<double[]> tempDataset = new ArrayList<>();
        
        ProblemDatasets tempProblemDatasets; // holds the trainingset and validation set from DatasetSpliter
        for (int i=0; i<folds.length; i++) {
            problemDatasets[i] = new ProblemDatasets();
            
            for (int j=0; j<folds.length; j++) {
                if (i == j) {
                    problemDatasets[i].testingDataset = Dataset.create(folds[i], folds[i][0].length -1);
                }
                else {
                    tempDataset.addAll(Arrays.asList(folds[j]));
                }
            }
            
            tempProblemDatasets = DatasetSplitter.split(
                    DatasetSpecs.init(tempDataset.toArray(new double[][]{}))
                        .setTrainingSize(1 - validationPercent)
                        .setValidationSize(validationPercent)
                        .setup()
            );
            
            problemDatasets[i].trainingDataset = tempProblemDatasets.trainingDataset;
            problemDatasets[i].validationDataset = tempProblemDatasets.validationDataset;
                    
            tempDataset.clear();
        }
        
        return problemDatasets;
    }
    
    public static ProblemDatasets[] custom(DatasetSpecs datasetSpecs, int foldsNumber) {
        int itemsPerFold = datasetSpecs.getDataSize() / foldsNumber;

        if (itemsPerFold < 1) throw new IllegalArgumentException(
                "The folds size is less than one. Fold size: '"+itemsPerFold+"' because the folds number ("+foldsNumber+") is to big."
        );
        
        ProblemDatasets[] folds = new ProblemDatasets[foldsNumber];
        
        int extraItems = datasetSpecs.getDataSize() % foldsNumber;
        
        double[][] data = datasetSpecs.getData();
        
        int startPoint = 0;
        int tempLength;
        double[][] tempDataset;
        for (int i=0; i<folds.length; i++) {
            if (i < extraItems) {
                tempLength = itemsPerFold + 1;
                tempDataset = new double[tempLength][];
                
                System.arraycopy(data, startPoint, tempDataset, 0, tempLength);
                datasetSpecs.setData(tempDataset);
                
                folds[i] = DatasetSplitter.split(datasetSpecs);
                
                startPoint += tempLength;
            }
            else {
                tempLength = itemsPerFold;
                tempDataset = new double[tempLength][];

                System.arraycopy(data, startPoint, tempDataset, 0, tempLength);
                datasetSpecs.setData(tempDataset);
                
                folds[i] = DatasetSplitter.split(datasetSpecs);
                
                startPoint += tempLength;
            }
        }
        
        return folds;
    } 
    
    private CrossValidation(){}
    
    public static void main(String[] args) {
//        double[][] test = {
//            {1,2,-1},
//            {3,4,-1},
//            {5,6,-1},
//            {7,8,-1},
//            {9,10,-1},
//            {11,12,-1},
//            {12,13,-1},
//            {14,15,-1},
//            {16,17,-1},
//            {18,19,-1}
//        };
//        
//        ProblemDatasets[] folds = kFolds(test, 3, 0.25);
//        int i=1;
//        for (ProblemDatasets f : folds) {
//            System.out.println("\nDataset: "+i);
//            
////            for (int j=0; j<f.trainingDataset.SIZE; j++) {
//                System.out.print(f.trainingDataset.SIZE+" "+f.validationDataset.SIZE+" "+f.testingDataset.SIZE+" ");
////                System.out.print(Arrays.toString(f.trainingDataset.features[j]));
////                System.out.println(Arrays.toString(f.testingDataset.features[j]));                
////            }
//
//            i++;
//        }

        long a = Double.doubleToLongBits(0.20);        
        long b = Double.doubleToLongBits(0.20);
        System.out.println(Long.toBinaryString(a));
//        System.out.println(Double.to);
        System.out.println(Double.longBitsToDouble(a+b));

    }
}

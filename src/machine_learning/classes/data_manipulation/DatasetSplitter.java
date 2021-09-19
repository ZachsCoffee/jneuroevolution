package data_manipulation;

import java.util.Arrays;

public final class DatasetSplitter {

    private DatasetSplitter(){}

    /**
     * Splits the data into sets, based the datasetSpecs
     * @param datasetSpecs Specifies the size of each data set and the targets count.
     * @return A new problem datasets object.
     */
    public static ProblemDatasets split(DatasetSpecs datasetSpecs) {
        ProblemDatasets problemDatasets = new ProblemDatasets();

        int features = datasetSpecs.getData()[0].length - datasetSpecs.getTargetsCount();

        double[][][] datasets = null;
        
        if (datasetSpecs.haveTrainingSet()) {
            datasets = split(datasetSpecs.getData(), datasetSpecs.getTrainingSize());
            
            problemDatasets.trainingDataset = Dataset.create(datasets[0], features);
        }
        
        if (datasetSpecs.haveValidationSet()) {
            if (datasets == null) {
                datasets = split(datasetSpecs.getData(), datasetSpecs.getValidationSize());
                problemDatasets.validationDataset = Dataset.create(datasets[0], features);

            }
            else {
                datasets = split(datasets[1], datasetSpecs.getValidationSize());
                problemDatasets.validationDataset = Dataset.create(datasets[0], features);;
            }
        }
        
        if (datasetSpecs.haveTestingSet()) {
            if (datasets == null) {
                datasets = split(datasetSpecs.getData(), datasetSpecs.getTestingSize());
                problemDatasets.testingDataset = Dataset.create(datasets[0], features);
            }
            else {
                problemDatasets.testingDataset = Dataset.create(split(datasets[1], datasetSpecs.getTestingSize())[0], features);
            }
        }

        return problemDatasets;
    }

    /**
     * Splits the data with cross over. Returns new arrays.
     * @param data The data to be crossover split.
     * @param firstSetSize The size of the first dataset
     * @return The new cross over split arrays.
     */
    private static double[][][] split(double[][] data, int firstSetSize) {
        int[][] crossData = split(firstSetSize, data.length);

        final int firstDatasetPosition = 0, secondDatasetPosition = 1;

        double[][] dataset1 = new double[crossData[firstDatasetPosition].length][];
        for (int i=0; i<crossData[firstDatasetPosition].length; i++) {
            dataset1[i] = data[crossData[firstDatasetPosition][i]];
        }

        double[][] dataset2 = new double[crossData[secondDatasetPosition].length][];
        for (int i=0; i<crossData[secondDatasetPosition].length; i++) {
            dataset2[i] = data[crossData[secondDatasetPosition][i]];
        }

        return new double[][][] {dataset1, dataset2};
    }
    
    /**
     * Split dataset without access the actual data.
     * @param trainingSize
     * @param totalSize
     * @return A 2D array. The first row is the training set the second row is the testing set. The return data is a map with the positions of the actual data.
     */
    public static int[][] split(int trainingSize, int totalSize){
        if (totalSize <=0 || trainingSize <=0) throw new IllegalArgumentException(
                "All arguments must be greater zero. Totalsize: "+totalSize+" TrainingSize: "+trainingSize
        );
        
        if (totalSize < trainingSize) throw new IllegalArgumentException(
                "Total size must be greater than trainingSize. Total size: "+totalSize+" trainingSize: "+trainingSize
        );
        
        
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

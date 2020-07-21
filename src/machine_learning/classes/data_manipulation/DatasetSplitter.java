package data_manipulation;

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

        double[][][] datasets = split(datasetSpecs.getData(), datasetSpecs.getTrainingSize());
        problemDatasets.trainingDataset = Dataset.create(datasets[0], features);

        if (datasetSpecs.haveValidationSet()) {
            datasets = split(datasets[1], datasetSpecs.getTestingSize());

            problemDatasets.testingDataset = Dataset.create(datasets[0], features);

            problemDatasets.validationDataset = Dataset.create(datasets[1], features);
        }
        else {
            problemDatasets.testingDataset = Dataset.create(datasets[1], features);
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
        int[][] crossData = CrossValidation.crossValidation(firstSetSize, data.length);

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
}

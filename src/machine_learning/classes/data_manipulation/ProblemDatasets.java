package data_manipulation;

public class ProblemDatasets {
    Dataset trainingDataset, testingDataset, validationDataset;
    
    public Dataset getTrainingDataset() {
//        if (datasetId == -1) throw new RuntimeException(
//            "Can't use the dataset if not specified a unique id."
//        );
        
        return trainingDataset;
    }

    public Dataset getTestingDataset() {
//        if (datasetId == -1) throw new RuntimeException(
//            "Can't use the dataset if not specified a unique id."
//        );
                
        return testingDataset;
    }

    public Dataset getValidationDataset() {
//        if (datasetId == -1) throw new RuntimeException(
//            "Can't use the dataset if not specified a unique id."
//        );
        
        return validationDataset;
    }
}

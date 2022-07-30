package data_manipulation;

public class ProblemDatasets {
    RawDataset trainingRawDataset, testingRawDataset, validationRawDataset;
    
    public RawDataset getTrainingDataset() {
//        if (datasetId == -1) throw new RuntimeException(
//            "Can't use the dataset if not specified a unique id."
//        );
        
        return trainingRawDataset;
    }

    public RawDataset getTestingDataset() {
//        if (datasetId == -1) throw new RuntimeException(
//            "Can't use the dataset if not specified a unique id."
//        );
                
        return testingRawDataset;
    }

    public RawDataset getValidationDataset() {
//        if (datasetId == -1) throw new RuntimeException(
//            "Can't use the dataset if not specified a unique id."
//        );
        
        return validationRawDataset;
    }
}

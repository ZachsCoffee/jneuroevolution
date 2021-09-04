package data_manipulation;

import maths.Maths;

public final class DatasetSpecs {
    
    public static DatasetSpecs init(double[][] data) {
        
        return new DatasetSpecs(data);
    }
    
    public static DatasetSpecs init(double[][] data, double trainingPercent, double testingPercent) {
        return new DatasetSpecs(data, trainingPercent, testingPercent);
    }
    
    private boolean haveSetupCalled = false;
    
    private double[][] data;
    private double 
            trainingPercent, 
            testingPercent, 
            validationPercet;
    
    private int
            trainingSize,
            testingSize,
            validationSize,
            targetsCount = 1;

    private DatasetSpecs(double[][] data) {
        if (data == null) throw new IllegalArgumentException(
                "Argument data not null"
        );

        this.data = data;
    }
    
    private DatasetSpecs(double[][] data, double trainingPercent, double testingPercent) {
        this(data);
        
        setBasicSets(trainingPercent, testingPercent);
    }

    private void setBasicSets(double trainingPercent, double testingPercent) {
        checkPercent(trainingPercent, "Training");
        checkPercent(testingPercent, "Testing");
        
        this.trainingPercent = trainingPercent;
        this.testingPercent = testingPercent;
        
        checkPercent(trainingPercent, "Training");
        
        this.trainingSize = (int) (data.length * trainingPercent);
        this.testingSize = (int) (data.length * testingPercent);
        
        if (trainingSize + testingSize > data.length) throw new IllegalArgumentException(
                "Training size with testing size is must smaller than data size. Training size: "+trainingSize+" Testing size: "+testingSize+" Data size: "+data.length
        );
    }
    
    public DatasetSpecs setTrainingSize(double trainingPercent) {
        checkPercent(trainingPercent, "Training");
        
        this.trainingPercent = trainingPercent;
        this.trainingSize = (int) (data.length * trainingPercent);
        
        checkSumOfPercents();
        
        return this;
    }
    
    public DatasetSpecs setTestingSize(double testingPercent) {
        checkPercent(testingPercent, "Testing");
        
        this.testingPercent = testingPercent;
        this.testingSize = (int) (data.length * testingPercent);
        
        checkSumOfPercents();
        
        return this;
    }
            
    
    public DatasetSpecs setValidationSize(double validationPercent) {
        checkPercent(validationPercent, "Validation");
        
        this.validationPercet = validationPercent;
        this.validationSize = (int) (data.length * validationPercent);
        
        checkSumOfPercents();
        
        return this;
    }

    public DatasetSpecs setTargetsCount(int targetsCount) {
        checkSize(targetsCount, "Targets count must be positive number");

        this.targetsCount = targetsCount;

        return this;
    }
    
    public int getDatasetsLength() {
        return trainingSize + testingSize + validationSize;
    }

    public int getDataSize() {
        return data.length;
    }
    
    public double[][] getData() {
        return data;
    }

    public int getTrainingSize() {
        if ( ! haveSetupCalled) throw new RuntimeException("Can't use the sets if the setup method not called.");
        return trainingSize;
    }

    public int getTestingSize() {
        if ( ! haveSetupCalled) throw new RuntimeException("Can't use the sets if the setup method not called.");

        return testingSize;
    }

    public int getValidationSize() {
        if ( ! haveSetupCalled) throw new RuntimeException("Can't use the sets if the setup method not called.");

        return validationSize;
    }

    public int getTargetsCount() {
        
        return targetsCount;
    }
    
    public DatasetSpecs setup() {
        int diference = Maths.distance(trainingSize + testingSize + validationSize, data.length);
        
        if (diference != 0) {
            if (haveTrainingSet()) {
                trainingSize += diference;
            }
            else if (haveTestingSet()) {
                testingSize += diference;
            }
            else if (haveValidationSet()) {
                validationSize += diference;
            }
            else {
                throw new RuntimeException(
                        "Call of setup method withouth a set size specified. Please specified at least one set and then call this method."
                );
            }
        }
        
        haveSetupCalled = true;
        
        return this;
    }
    
    public boolean haveTrainingSet() {
        return trainingSize > 0;
    }
    
    public boolean haveTestingSet() {
        return testingSize > 0;
    }
    
    public boolean haveValidationSet() {
        return validationSize > 0;
    }

    void setData(double[][] newData) {
        if (newData == null) throw new IllegalArgumentException(
            "Data argument not null."
        );
        
        // revalidate the sets sizes
        setBasicSets(trainingPercent, testingPercent);
        
        if (haveValidationSet()) setValidationSize(validationPercet);
    }
    
    private void checkSumOfPercents() {
        if (this.trainingPercent + testingPercent + validationPercet > 1) throw new IllegalArgumentException(
                "The sum of percents can't be greater than one"
        );
    }
    
    private void checkSize(int size, String errorMessage) {
        if (size <= 0) throw new IllegalArgumentException(errorMessage);
    }
    
    private void checkPercent(double percent, String message) {
        if (percent < 0 || percent > 1) throw new IllegalArgumentException(
                message+" percent range must be [0, 1]"
        );
    }
}

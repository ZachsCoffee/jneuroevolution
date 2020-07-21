package data_manipulation;

public final class DatasetSpecs {

    // TODO: MAKE THIS CLASS BUILDER, CHECK AT THE BUILD TIME IF THE COUNT OF SETS SIZES IS EQUAL TO DATA LENGTH

    public static DatasetSpecs init(double[][] data, int trainingSize, int testingSize) {
        return new DatasetSpecs(data, trainingSize, testingSize);
    }

    public static DatasetSpecs init(double[][] data, double trainingPercent, double testingPercent) {
        checkPercent(trainingPercent, "Training");
        checkPercent(testingPercent, "Testing");

        return new DatasetSpecs(data, (int) (data.length * trainingPercent), (int) (data.length * testingPercent));
    }

    private static void checkPercent(double percent, String message) {
        if (percent < 0 || percent > 1) throw new IllegalArgumentException(
                message+" percent range must be [0, 1]"
        );
    }

    private double[][] data;
    private int
            trainingSize,
            testingSize,
            validationSize,
            targetsCount;

    private DatasetSpecs(double[][] data, int trainingSize, int testingSize) {
        if (data == null) throw new IllegalArgumentException(
                "Argument data not null"
        );

        checkSize(trainingSize, "Training size must be positive number");
        checkSize(testingSize, "Testing size must be positive number");

        if (trainingSize + testingSize > data.length) throw new IllegalArgumentException(
                "Training size with testing size is must smaller than data size. Training size: "+trainingSize+" Testing size: "+testingSize+" Data size: "+data.length
        );

        this.data = data;
        this.trainingSize = trainingSize;
        this.testingSize = testingSize;
        this.targetsCount = 1;
    }

    public DatasetSpecs setValidationSize(double validationPercent) {
        checkPercent(validationPercent, "Validation");

        return setValidationSize((int) (data.length * validationPercent));
    }

    public DatasetSpecs setValidationSize(int validationSize) {
        checkSize(validationSize, "Validation size must be positive number");

        if (trainingSize + testingSize + this.validationSize > data.length) throw new IllegalArgumentException(
                "The sum of sets size must be smaller than data size"
        );

        this.validationSize = validationSize;

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

    public double[][] getData() {
        return data;
    }

    public int getTrainingSize() {
        return trainingSize;
    }

    public int getTestingSize() {
        return testingSize;
    }

    public int getValidationSize() {
        return validationSize;
    }

    public int getTargetsCount() {
        return targetsCount;
    }

    public boolean haveValidationSet() {
        return validationSize > 0;
    }

    private void checkSize(int size, String errorMessage) {
        if (size <= 0) throw new IllegalArgumentException(errorMessage);
    }
}

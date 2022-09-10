package execution;

public class ResultsData {
    public final double[] evolutionStatistics, validationStatistics;
    public final double[][] realData, predictedData;
    public final String resultsString;

    ResultsData(double[] evolutionStatistics, double[] validationStatistics, double[][] realData, double[][] predictedData, String resultsString) {
        this.evolutionStatistics = evolutionStatistics;
        this.validationStatistics = validationStatistics;
        this.realData = realData;
        this.predictedData = predictedData;
        this.resultsString = resultsString;
    }
}

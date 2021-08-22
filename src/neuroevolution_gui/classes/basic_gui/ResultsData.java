package basic_gui;

public class ResultsData {
    public double[] evolutionStatistics, validationStatistics;
    public double[][] realData, predictedData;
    public String resultsString;

    public ResultsData(double[] evolutionStatistics, double[] validationStatistics, double[][] realData, double[][] predictedData, String resultsString) {
        this.evolutionStatistics = evolutionStatistics;
        this.validationStatistics = validationStatistics;
        this.realData = realData;
        this.predictedData = predictedData;
        this.resultsString = resultsString;
    }
}

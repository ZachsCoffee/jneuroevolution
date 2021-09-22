package basic_gui;

public class ResultsData {
    public float[] evolutionStatistics;
    public float[] validationStatistics;
    public float[][] realData;
    public float[][] predictedData;
    public String resultsString;

    public ResultsData(float[] evolutionStatistics, float[] validationStatistics, float[][] realData, float[][] predictedData, String resultsString) {
        this.evolutionStatistics = evolutionStatistics;
        this.validationStatistics = validationStatistics;
        this.realData = realData;
        this.predictedData = predictedData;
        this.resultsString = resultsString;
    }
}

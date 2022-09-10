package convolution_test.stl10;

import basic_gui.Gui;
import core.layer.TrainableLayer;
import dataset.MatrixReaderDataset;
import execution.EvaluationTarget;
import execution.ExecutionResponse;
import execution.ProblemExecutor;
import execution.common.DataBinder;

public class Stl10ProblemExecutor extends ProblemExecutor<TrainableLayer, MatrixReaderDataset, Stl10ConvolutionProblem> {

    public static void main(String[] args) {
        Gui.create(Stl10ProblemExecutor.class);
    }

    public Stl10ProblemExecutor(
        DataBinder dataBinder
    ) {
        super(dataBinder, new Stl10ConvolutionProblem());
        epochs = Stl10ConvolutionProblem.EPOCHS;
        threads = 5;
        populationSize = 10;
        evaluationTarget = EvaluationTarget.VALIDATION_BEST;
    }

    @Override
    public void executionEnds(ExecutionResponse[] responses) {

    }

    @Override
    protected EvaluationResult evaluation(TrainableLayer layer, MatrixReaderDataset dataset) {
        double[][] predictions = new double[dataset.getDataLength()][2];
        int totalError = 0;
        for (int i = 0; i < dataset.getDataLength(); i++) {
            double[][] results = getProblem().evaluateSystemAtIndex(layer, dataset, i);
            int targetIndex = (int) results[1][0];
//            predictions[i] = new double[] {Math.round(results[0][targetIndex]), results[1][0]};
            int predictedIndex = bestIndex(results[0]);

            if (predictedIndex != targetIndex) {
                totalError++;
            }

            predictions[i][0] = predictedIndex;
            predictions[i][1] = targetIndex;
        }

        return new EvaluationResult(predictions, totalError);
    }

    private int bestIndex(double[] results) {
        double maxValue = results[0];
        int maxValueIndex = 0;
        for (int i = 1; i < results.length; i++) {
            if (results[i] > maxValue) {
                maxValue = results[i];
                maxValueIndex = i;
            }
        }

        return maxValueIndex;
    }
}

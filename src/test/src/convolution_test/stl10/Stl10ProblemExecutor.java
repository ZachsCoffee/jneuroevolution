package convolution_test.stl10;

import basic_gui.Gui;
import core.layer.TrainableLayer;
import dataset.MatrixReaderDataset;
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
        epochs = 10;
        threads = 5;
        populationSize = 10;
    }

    @Override
    public void executionEnds(ExecutionResponse[] responses) {

    }

    @Override
    protected EvaluationResult evaluation(TrainableLayer layer, MatrixReaderDataset dataset) {
        double[][] predictions = new double[dataset.getDataLength()][];
        int totalError = 0;
        for (int i = 0; i < dataset.getDataLength(); i++) {
            predictions[i] = getProblem().evaluateSystemAtIndex(layer, dataset, i);
            if (predictions[i][0] != dataset.getTargets()[i][0]) {
                totalError++;
            }
        }

        return new EvaluationResult(predictions, totalError);
    }
}

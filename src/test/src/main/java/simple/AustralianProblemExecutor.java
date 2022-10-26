package simple;

import basic_gui.Gui;
import data_manipulation.RawDataset;
import execution.EvaluationTarget;
import execution.ExecutionResponse;
import execution.ProblemExecutor;
import execution.common.DataBinder;
import networks.interfaces.PartialNetwork;

public class AustralianProblemExecutor extends ProblemExecutor<PartialNetwork, RawDataset, AustralianCreditProblem> {

    public static void main(String[] args) {
        Gui.create(AustralianProblemExecutor.class);
    }

    public AustralianProblemExecutor(DataBinder dataBinder) {
        super(dataBinder, new AustralianCreditProblem());

        epochs = AustralianCreditProblem.EPOCHS;
        populationSize = 30;
        threads = 10;
        personMigration = true;
        migrationPercent = .25;
        evaluationTarget = EvaluationTarget.EVOLUTION_BEST;
    }

    @Override
    public void executionEnds(ExecutionResponse[] responses) {

    }

    @Override
    protected EvaluationResult evaluation(PartialNetwork person, RawDataset dataset) {
        double[][] predictions = new double[dataset.SIZE][2];

        int fitness = 0;

        for (int i = 0; i < dataset.SIZE; i++) {
            double[] results = person.compute(dataset.getFeatures()[i]);

            long prediction = Math.round(results[0]);

            if (prediction == dataset.getTargets()[i][0]) {
                fitness++;
            }

            predictions[i][0] = prediction;
            predictions[i][1] = dataset.getTargets()[i][0];
        }
        System.out.println("Accuracy: " + ((double)fitness / dataset.SIZE));
        return new EvaluationResult(predictions, dataset.SIZE - fitness);
    }
}

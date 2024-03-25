/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution;

import data_manipulation.DatasetTarget;
import data_manipulation.DatasetType;
import execution.common.SupervisedProblem;
import maths.LinearValues;
import maths.MinMax;

/**
 * @author Zachs
 */
public abstract class Problem<P, D extends DatasetTarget> implements SupervisedProblem<P, D> {

    protected D
        trainingDataset,
        validationDataset,
        testingDataset;
    private LinearValues dynamicMutation = null;
    private int fixedMutation;

    public D getTrainingDataset() {
//        System.out.println(Thread.currentThread().getId());

        return trainingDataset;
    }

    public D getValidationDataset() {
//        System.out.println(Thread.currentThread().getId());

        return validationDataset;
    }

    public D getTestingDataset() {
//        System.out.println(Thread.currentThread().getId());

        return testingDataset;
    }

    public void setFixedMutation(int mutation) {
        if (mutation <= 0) throw new IllegalArgumentException("Muation must be grater than zero");

        fixedMutation = mutation;
    }

    public final void setDynamicMutation(MinMax mutationValues, int epochs) {
        dynamicMutation = new LinearValues(mutationValues, epochs, LinearValues.Order.DESC);
    }

    public int getMutationChange(int currentEpoch) {
        if (dynamicMutation != null) {
            return dynamicMutation.compute(currentEpoch);
        }
        else {
            return fixedMutation;
        }
    }

    public D getDataset(DatasetType type) {
        switch (type) {
            case TRAINING:
                return getTrainingDataset();
            case VALIDATION:
                return getValidationDataset();
            case TESTING:
                return getTestingDataset();
            default:
                throw new IllegalArgumentException("Unexpected type: " + type);
        }
    }
}

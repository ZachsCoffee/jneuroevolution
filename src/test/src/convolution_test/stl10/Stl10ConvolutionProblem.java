package convolution_test.stl10;

import dataset.common.DatasetMatrixReader;
import evolution.Convolution2DProblem;
import evolution.ConvolutionPersonManager;
import evolution_builder.population.PopulationPerson;
import execution.Problem;
import executors.common.TrainableConvolution;

public class Stl10ConvolutionProblem implements Convolution2DProblem<PopulationPerson<ConvolutionPersonManager>> {

    @Override
    public Problem getProblem() {
        return null;
    }

    @Override
    public TrainableConvolution buildConvolution() {
        return null;
    }

    @Override
    public double evaluateFitness(TrainableConvolution convolution, DatasetMatrixReader channels) {
        return 0;
    }
}

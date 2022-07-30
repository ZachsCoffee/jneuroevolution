package evolution;

import dataset.common.DatasetMatrixReader;
import evolution_builder.population.PopulationPerson;
import execution.Problem;
import executors.common.TrainableConvolution;

public interface Convolution2DProblem<P extends PopulationPerson<?>> {

    Problem<P, DatasetMatrixReader> getProblem();

    TrainableConvolution buildConvolution();

    double evaluateFitness(TrainableConvolution convolution, DatasetMatrixReader channels);
}

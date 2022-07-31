package evolution;

import dataset.MatrixReaderDataset;
import execution.common.CommonProblem;
import executors.common.TrainableConvolution;

public interface Convolution2DProblem<P> extends CommonProblem<P, MatrixReaderDataset> {

    TrainableConvolution buildConvolution();

    double evaluateFitness(TrainableConvolution convolution, MatrixReaderDataset dataset);
}

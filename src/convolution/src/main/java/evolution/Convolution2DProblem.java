package evolution;

import core.layer.TrainableLayer;
import dataset.MatrixReaderDataset;
import execution.common.SupervisedProblem;

public interface Convolution2DProblem extends SupervisedProblem<TrainableLayer, MatrixReaderDataset> {

    TrainableLayer buildConvolution();

    double evaluateFitness(TrainableLayer convolution, MatrixReaderDataset dataset);
}

package evolution;

import core.layer.TrainableLayer;
import dataset.MatrixReaderDataset;
import execution.common.CommonProblem;

public interface Convolution2DProblem<P> extends CommonProblem<P, MatrixReaderDataset> {

    TrainableLayer buildConvolution();

    double evaluateFitness(TrainableLayer convolution, MatrixReaderDataset dataset);
}

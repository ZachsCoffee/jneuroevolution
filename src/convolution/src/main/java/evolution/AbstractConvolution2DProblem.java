package evolution;

import core.layer.TrainableLayer;
import dataset.MatrixReaderDataset;
import execution.Problem;

public abstract class AbstractConvolution2DProblem extends Problem<TrainableLayer, MatrixReaderDataset> implements Convolution2DProblem {

    public abstract double[] evaluateSystemAtIndex(
        TrainableLayer convolution,
        MatrixReaderDataset dataset,
        int index
    );
}

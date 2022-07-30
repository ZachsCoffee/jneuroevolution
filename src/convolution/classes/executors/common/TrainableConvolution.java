package executors.common;

import layers.TrainableLayer;
import utils.common.Imitable;

public interface TrainableConvolution extends Convolution, Iterable<TrainableLayer>, Imitable<TrainableConvolution> {
    double getWeightAt(int index);

    void setWeightAt(int index, double weight);

    int getTotalWeights();
}

package executors.common;

import layer.Imitable;
import layer.TrainableLayer;

public interface TrainableConvolution extends Convolution, Iterable<TrainableLayer>, Imitable<TrainableConvolution> {
    double getWeightAt(int index);

    void setWeightAt(int index, double weight);

    int getTotalWeights();
}

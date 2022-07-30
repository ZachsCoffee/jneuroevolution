package layers;

import utils.common.Imitable;

public interface TrainableLayer extends Layer, Cloneable, Imitable<TrainableLayer> {
    int getWeightsCount();

    void setWeightAt(int index, double weight);

    double getWeightAt(int index);

    int getOutputChannelsCount();
}

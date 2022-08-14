package core.layer;

public interface TrainableLayer extends Layer, Cloneable, Imitable<TrainableLayer> {
    int getTotalWeights();

    void setWeightAt(int index, double weight);

    double getWeightAt(int index);

    int getOutputChannelsCount();
}

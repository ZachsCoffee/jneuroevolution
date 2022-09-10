package core.layer;

public interface TrainableLayer extends Layer, Cloneable {
    int getTotalWeights();

    void setWeightAt(int index, double weight);

    double getWeightAt(int index);

    int getOutputChannelsCount();
}

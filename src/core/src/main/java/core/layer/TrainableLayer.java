package core.layer;

public interface TrainableLayer extends Layer, Cloneable, CountableOutput {
    int getTotalWeights();

    void setWeightAt(int index, double weight);

    double getWeightAt(int index);
}

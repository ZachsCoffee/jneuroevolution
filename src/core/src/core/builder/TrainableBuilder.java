package core.builder;

import core.layer.TrainableLayer;

public interface TrainableBuilder<T> extends ChainableBuilder<T> {
    TrainableLayer build();
}

package core.builder;

import core.layer.TrainableLayer;

public interface TrainableBuilder extends ChainableBuilder {
    TrainableLayer build();
}

package core.builder;

import core.layer.Layer;

public interface LayerBuilder<T> extends ChainableBuilder<T> {
    Layer build();
}

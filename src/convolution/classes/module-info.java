module convolution {
    requires utils;
    requires java.desktop;
    requires machine_learning;
    requires org.apache.commons.lang3;
    requires j.text.utils;
    requires evolution_algorithms;
    requires neuroevolution;
    requires core;

    exports layers.trainable;
    exports layers.convolution;
    exports schema;
    exports filters;
    exports executors;
    exports input;
    exports layers.pool;
    exports layers;
    exports layers.flatten;
    exports utils;
    exports executors.common;
    exports evolution;
    exports dataset.common;
    exports dataset;
    exports builder;
}
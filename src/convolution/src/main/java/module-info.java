module convolution {
    requires utils;
    requires java.desktop;
    requires machine_learning;
    requires j.text.utils;
    requires evolution_algorithms;
    requires neuroevolution;
    requires core;

    exports layers.trainable;
    exports layers.convolution;
    exports filters;
    exports executors;
    exports input;
    exports layers.pool;
    exports layers.flatten;
    exports utils;
    exports evolution;
    exports dataset;
    exports builder;
    exports schema;
    exports multithreaded;
}
module convolution {
    requires utils;
    requires java.desktop;
    requires machine_learning;
    requires j.text.utils;

    exports layers.convolution;
    exports schema;
    exports filters;
    exports executors;
    exports input;
    exports layers.pool;
    exports layers;
}
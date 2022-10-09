module test {
    requires convolution;
    requires machine_learning;
    requires utils;
    requires org.apache.commons.lang3;
    requires java.desktop;
    requires neuroevolution;
    requires evolution_algorithms;
    requires neuroevolution_gui;
    requires core;
    requires builder;

    exports convolution_test;
    exports convolution_test.stl10;
    exports simple;
}
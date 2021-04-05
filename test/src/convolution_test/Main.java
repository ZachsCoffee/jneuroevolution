package convolution_test;

import convolution.ConvolutionLayer;
import executors.ConvolutionExecutor;
import executors.ConvolutionParallelExecutor;
import filters.Filter;
import filters.Kernel;
import functions.ActivationFunctions;
import input.HsbInput;
import pool.PoolFunction;
import pool.PoolLayer;

import java.io.File;
import java.nio.file.DirectoryStream;

public class Main {

    public static void main(String[] args) {
        Filter[] filters = {
                new Filter(Kernel.SHARPEN, ActivationFunctions.groundRelu()),
                new Filter(Kernel.EDGE_DETECTION_HIGH, ActivationFunctions.groundRelu()),
                new Filter(Kernel.EDGE_DETECTION_MEDIUM, ActivationFunctions.groundRelu()),
                new Filter(Kernel.EDGE_DETECTION_SOFT, ActivationFunctions.groundRelu()),
                new Filter(Kernel.IDENTITY, ActivationFunctions.groundRelu()),
        };

        Filter[] filters2 = {
                new Filter(Kernel.CUSTOM_1, ActivationFunctions.groundRelu()),
                new Filter(Kernel.CUSTOM_2, ActivationFunctions.groundRelu()),
                new Filter(Kernel.CUSTOM_3, ActivationFunctions.groundRelu()),
                new Filter(Kernel.CUSTOM_4, ActivationFunctions.groundRelu()),
                new Filter(Kernel.CUSTOM_5, ActivationFunctions.groundRelu()),
        };

        ConvolutionParallelExecutor.initialize(new HsbInput(new File("/home/zachs/Downloads/ΟΔ 5396.jpg")))
                .addLayerForAllChannels(new ConvolutionLayer(filters, 1, true))
                .addLayerForAllChannels(new ConvolutionLayer(filters2, 1, true))
                .addLayerForAllChannels(new PoolLayer(PoolFunction.AVERAGE, 3, 3))
                .printSchema();
    }
}

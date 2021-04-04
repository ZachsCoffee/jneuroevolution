package convolution_test;

import convolution.ConvolutionLayer;
import executors.ConvolutionExecutor;
import filters.Kernel;
import functions.ActivationFunctions;
import input.HsbInput;

import java.io.File;
import java.nio.file.DirectoryStream;

public class Main {
    public static void main(String[] args) {
//        ConvolutionExecutor.initialize(new HsbInput(new File("/home/zachs/Downloads/ΟΔ 5396.jpg")))
//                .addLayerForAllChannels(new ConvolutionLayer(new DirectoryStream.Filter(Kernel.SHARPEN, ActivationFunctions.groundRelu()), 1, true))
//                .executeParallel(readers -> System.out.println("Done!"));

    }
}

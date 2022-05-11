package convolution_test.stl10;

import cli.stdout.StdOut;
import executors.ConvolutionExecutor;
import filters.Filter;
import filters.Kernel;
import functions.ActivationFunction;
import input.HsbInput;
import layers.convolution.ConvolutionLayer;
import layers.flatten.FlatLayer;
import layers.pool.PoolFunction;
import layers.pool.PoolLayer;
import utils.MatrixUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class Benchmark {

    private static final Filter[] filters1 = {
        new Filter(Kernel.SHARPEN, ActivationFunction.GROUND_RELU.getFunction()),
        new Filter(Kernel.EDGE_DETECTION_HIGH, ActivationFunction.GROUND_RELU.getFunction()),
//                new Filter(Kernel.EDGE_DETECTION_MEDIUM, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.EDGE_DETECTION_SOFT, ActivationFunction.GROUND_RELU.getFunction()),
        new Filter(Kernel.SOBEL_EDGE_HORIZONTAL, ActivationFunction.GROUND_RELU.getFunction()),
        new Filter(Kernel.SOBEL_EDGE_VERTICAL, ActivationFunction.GROUND_RELU.getFunction()),
        new Filter(Kernel.IDENTITY, ActivationFunction.GROUND_RELU.getFunction()),
//        new Filter(Kernel.SHARPEN, ActivationFunction.GROUND_RELU.getFunction()),
//        new Filter(Kernel.SHARPEN2, ActivationFunction.GAUSS.getFunction()),
//                new Filter(Kernel.IDENTITY, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.SHARPEN, ActivationFunctions.groundRelu()),

//                new Filter(Kernel.IDENTITY, ActivationFunctions.groundRelu()),
    };

    private static final Filter[] filters2 = {
        new Filter(Kernel.SHARPEN, ActivationFunction.GROUND_RELU.getFunction()),
//        new Filter(Kernel.EDGE_DETECTION_HIGH, ActivationFunction.GROUND_RELU.getFunction()),
//                new Filter(Kernel.EDGE_DETECTION_MEDIUM, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.EDGE_DETECTION_SOFT, ActivationFunction.GROUND_RELU.getFunction()),
//        new Filter(Kernel.SOBEL_EDGE_HORIZONTAL, ActivationFunction.GROUND_RELU.getFunction()),
//        new Filter(Kernel.SOBEL_EDGE_VERTICAL, ActivationFunction.GROUND_RELU.getFunction()),
        new Filter(Kernel.IDENTITY, ActivationFunction.GROUND_RELU.getFunction()),
//        new Filter(Kernel.SHARPEN, ActivationFunction.GROUND_RELU.getFunction()),
//        new Filter(Kernel.SHARPEN2, ActivationFunction.GAUSS.getFunction()),
//                new Filter(Kernel.IDENTITY, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.SHARPEN, ActivationFunctions.groundRelu()),

//                new Filter(Kernel.IDENTITY, ActivationFunctions.groundRelu()),
    };

    public static void main(String[] args) throws IOException {
        Path mainPath = Paths.get("/home/zachs/Develop/MachineLearning/stl10_binary");
        System.out.println("Start building train dataset.");
        createConvolutedDataset(
            mainPath.resolve("images/train").toFile(),
            mainPath.resolve("images/convoluted/train.cov").toFile(),
            mainPath.resolve("train_y.bin").toFile()
        );
        System.out.println("\nStart building test dataset.");
        createConvolutedDataset(
            mainPath.resolve("images/test").toFile(),
            mainPath.resolve("images/convoluted/test.cov").toFile(),
            mainPath.resolve("test_y.bin").toFile()
        );
    }

    private static void createConvolutedDataset(
        File imagesDirectory,
        File datasetOutput,
        File labelData
    ) throws IOException {
        File[] files = Objects.requireNonNull(imagesDirectory.listFiles());

        Arrays.sort(files, Comparator.comparingInt(o -> Integer.parseInt(o.getName())));

        ConvolutionExecutor convolutionExecutor = getConvolutionExecutor(files[0], false);

        try (
            DataOutputStream xOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(datasetOutput)));
            BufferedInputStream yDatasetInputStream = new BufferedInputStream(new FileInputStream(labelData))
        ) {
            double[] featureData;
            boolean writeHeader = true;
            for (int i = 1; i < files.length; i++) {
                StdOut.printPercent(i, files.length -1, "Completed");
                featureData = MatrixUtils.mergeChannels(convolutionExecutor.execute().getChannelsOutput());

                int label = yDatasetInputStream.read() - 1;
                if (label < 0) {
                    throw new RuntimeException("Possible corrupted labelData: " + labelData);
                }

                ByteBuffer byteBuffer = ByteBuffer.allocate(featureData.length * 8);
                byteBuffer.asDoubleBuffer().put(featureData);

                if (writeHeader) {
                    xOutputStream.writeInt(featureData.length + 1);
                    writeHeader = false;
                }

                xOutputStream.write(byteBuffer.array());
                xOutputStream.writeDouble(label);


                convolutionExecutor.changeInput(new HsbInput(ImageIO.read(files[i])));
            }
        }
    }

    private static ConvolutionExecutor getConvolutionExecutor(File file, boolean printSchema) throws IOException {
        ConvolutionExecutor convolutionExecutor = ConvolutionExecutor.initialize(new HsbInput(ImageIO.read(file)), true)
            .addLayer(new ConvolutionLayer(filters1, 1).setSquashChannels(true))
            .addLayer(new PoolLayer(PoolFunction.AVERAGE, 5, 1))
            .addLayer(new ConvolutionLayer(filters1, 3).setSquashChannels(true))
            .addLayer(new PoolLayer(PoolFunction.AVERAGE, 5, 2))
//            .addLayerForAllChannels(new ConvolutionLayer(filters, 3))
//            .addLayerForAllChannels(new PoolLayer(PoolFunction.AVERAGE, 3, 4))
            .addLayer(new FlatLayer());

        if (printSchema) {
            convolutionExecutor.printSchema();
            System.exit(0);
        }

        return convolutionExecutor;
    }
}

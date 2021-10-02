package convolution_test;

import executors.ConvolutionExecutor;
import executors.ConvolutionParallelExecutor;
import filters.Filter;
import filters.Kernel;
import functions.ActivationFunction;
import input.*;
import layers.convolution.ConvolutionLayer;
import layers.flatten.FlatLayer;
import layers.pool.PoolFunction;
import layers.pool.PoolLayer;
import maths.matrix.MatrixReader;
import networks.multilayer_perceptron.network.NeuralNetwork;
import networks.multilayer_perceptron.serializers.NetworkJsonSerializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

public class ClearBackground {
    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("/home/zachs/Develop/Java/artificialintelligence/networks/network1.json"));

        NeuralNetwork neuralNetwork = NetworkJsonSerializer.fromJson(new String(bytes, StandardCharsets.UTF_8));

        Filter[] filters = {
//                new Filter(Kernel.SHARPEN, ActivationFunctions.groundRelu()),
                new Filter(Kernel.EDGE_DETECTION_HIGH, ActivationFunction.GROUND_RELU.getFunction()),
//                new Filter(Kernel.EDGE_DETECTION_MEDIUM, ActivationFunctions.groundRelu()),
                new Filter(Kernel.EDGE_DETECTION_SOFT, ActivationFunction.GROUND_RELU.getFunction()),
                new Filter(Kernel.SOBEL_EDGE_HORIZONTAL, ActivationFunction.GROUND_RELU.getFunction()),
                new Filter(Kernel.SOBEL_EDGE_VERTICAL, ActivationFunction.GROUND_RELU.getFunction()),
//                new Filter(Kernel.IDENTITY, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.SHARPEN, ActivationFunctions.groundRelu()),

//                new Filter(Kernel.IDENTITY, ActivationFunctions.groundRelu()),
        };
        File imageFile = new File("/home/zachs/Develop/Java/artificialintelligence/networks/object1.jpg");
        GridInput gridInput = new GridInput(
                new HsbInput(ImageIO.read(imageFile)),
                6,
                6
        );

        BufferedImage resultImage = new BufferedImage(1200, 1600, BufferedImage.TYPE_INT_RGB);

        BufferedImage testImage = ImageIO.read(imageFile);

        ConvolutionExecutor convolutionExecutor = null;
        for (ConvolutionInput convolutionInput : gridInput) {
            convolutionExecutor = ConvolutionParallelExecutor.initialize(convolutionInput)
                    .addLayerForAllChannels(new ConvolutionLayer(filters, 1))
//                .addLayerForAllChannels(new ConvolutionLayer(filters2, 1, true))
                    .addLayerForAllChannels(new PoolLayer(PoolFunction.AVERAGE, 2, 2))
//                    .addLayerForAllChannels(new ConvolutionLayer(filters, 10, 10))
                    .addLayerForAllChannels(new FlatLayer())
                    .execute();

            MatrixReader[][] channels = convolutionExecutor.getChannelsOutput();
            double[] features = new double[channels.length * channels[0][0].getColumnCount()];

            int copyOffset = 0;
            for (int i=0; i< channels.length; i++) {
                int copySize = channels[i][0].getRow(0).length;

                System.arraycopy(channels[i][0].getRow(0), 0, features, copyOffset, copySize);
                copyOffset += copySize;
            }

            double[] output = neuralNetwork.compute(features);
            GridInputIterator.GridBlock gridBlock = (GridInputIterator.GridBlock) convolutionInput.getChannels()[0];

            for (int i=0; i<output.length; i++) {
                int rowIndex = i / 6;
                int columnIndex = i % 6;

                int imageRow = gridBlock.getRealRow() + rowIndex;
                int imageColumn = gridBlock.getRealColumn() + columnIndex;

                if (Math.round(output[i]) == 1) {
                    resultImage.setRGB(imageColumn, imageRow, 0xFF << 16);
                } else {
                    resultImage.setRGB(imageColumn, imageRow, testImage.getRGB(imageColumn, imageRow));
                }
            }
        }

        ImageIO.write(resultImage, "jpg", new File("/home/zachs/Develop/Java/artificialintelligence/networks/output.jpg"));
    }
}

package convolution_test;

import executors.ConvolutionExecutor;
import executors.ConvolutionParallelExecutor;
import files.CSVFileWriter;
import layers.convolution.ConvolutionLayer;
import filters.Filter;
import filters.Kernel;
import functions.ActivationFunctions;
import input.HsbInput;
import layers.flatten.FlatLayer;
import layers.pool.PoolFunction;
import layers.pool.PoolLayer;
import maths.matrix.MatrixReader;
import utils.ConvolutionDataPresenter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
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

        String inputPath = "/home/zachs/Develop/Java/artificialintelligence/datasets/input_images";
        CSVFileWriter csvFileWriter = new CSVFileWriter("/home/zachs/Develop/Java/artificialintelligence/datasets/dataset2.csv");

        for (int i=2; i<=22; i++) {
            System.out.print("For image #"+i);
            ConvolutionExecutor output = convolveImage(filters, new File(inputPath + "/f"+i+".jpg"));
            createDataset(output.getChannelsOutput(), new File(inputPath + "/f"+i+"_final.jpg"), csvFileWriter);
            System.out.println(" DONE");
        }

        csvFileWriter.close();

//        outputAsImages(output.getChannelsOutput(), "/home/zachs/Develop/Java/artificialintelligence/datasets/test_images");
    }

    private static ConvolutionExecutor convolveImage(Filter[] filters, File image) {
        //        ConvolutionExecutor output = ConvolutionExecutor.initialize(new HsbInput(new File("/home/zachs/Downloads/ΟΔ 5396.jpg")))
         return new ConvolutionParallelExecutor(new HsbInput(image).getChannels())
                .addLayerForAllChannels(new ConvolutionLayer(filters, 1600, 1200))
//                .addLayerForAllChannels(new ConvolutionLayer(filters, 1, true))
//                .addLayerForAllChannels(new ConvolutionLayer(filters2, 1, true))
                .addLayerForAllChannels(new PoolLayer(PoolFunction.AVERAGE, 3, 3))
                .addLayerForAllChannels(new FlatLayer())
                .execute();
    }

    private static void createDataset(MatrixReader[][] convolutionData, File mask, CSVFileWriter csvFileWriter) throws IOException {
//        BufferedImage bufferedImage = ImageIO.read(image);
        BufferedImage bufferedMask = ImageIO.read(mask);
        int xLength = bufferedMask.getWidth() - 3;
        int yLength = bufferedMask.getHeight() - 3;
//        BufferedImage bufferedImage = new BufferedImage(bufferedMask.getWidth(), bufferedMask.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        int[] rgbArray = new int[9];
        Double[] feature = new Double[4];
        for (int x=0, i=0; x<xLength; x+=3) {
            for (int y=0; y<yLength; y+=3, i++) {
                int featureIndex = 0;
                for (MatrixReader[] channel: convolutionData) {
                    feature[featureIndex++] = channel[0].valueAt(0, i);
                }

//                System.out.println(x + " " + y);
                bufferedMask.getRGB(x, y, 3, 3, rgbArray, 0, 1);
                feature[featureIndex] = (double) averageColor(rgbArray);
                csvFileWriter.writeLine((Object[]) feature);
//                int temp = 255 * feature[featureIndex].intValue();
//                bufferedImage.setRGB(x, y, (temp << 16) | (temp << 8) | temp);
            }
        }

//        ImageIO.write(bufferedImage, "jpg", new File("/home/zachs/Develop/Java/artificialintelligence/datasets/test.jpg"));
    }

    private static int averageColor(int[] rgbArray) {
        int threshold = 0xFF / 2;
        int sum = 0;
        for (int rgb : rgbArray) {
            sum += rgb & 0xFF;
        }

        return 1 - (sum / 9 / threshold);
    }

    private static void outputAsImages(MatrixReader[][] channels, String folder) {
        int i = 0;
        for (MatrixReader[] channel : channels) {
            int j = 0;
            for (MatrixReader filter : channel) {
                ConvolutionDataPresenter.toBlackWhiteImage(
                        filter,
                        new File(folder+"/channel_"+i+"_filter_"+j+".jpg")
                );
                j++;
            }
            i++;
        }
    }
}

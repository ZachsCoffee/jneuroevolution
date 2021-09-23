package convolution_test;

import executors.ConvolutionExecutor;
import executors.ConvolutionParallelExecutor;
import files.binary.BinaryDatasetWriter;
import files.csv.CSVDatasetUtils;
import files.csv.CSVFileReader;
import files.csv.CSVFileWriter;
import input.*;
import layers.convolution.ConvolutionLayer;
import filters.Filter;
import filters.Kernel;
import functions.ActivationFunctions;
import layers.flatten.FlatLayer;
import layers.pool.PoolFunction;
import layers.pool.PoolLayer;
import maths.Maths;
import maths.matrix.Matrix;
import maths.matrix.MatrixReader;
import utils.ConvolutionDataPresenter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class Main {
    private static Integer featureLength = null;
    private static final int THRESHOLD = 128;
    private static Plot plot;
    
    public static void main(String[] args) throws IOException {
//        writeToBinary();
//        plot = new Plot();
//        plot.open();

        Filter[] filters = {
//                new Filter(Kernel.SHARPEN, ActivationFunctions.groundRelu()),
                new Filter(Kernel.EDGE_DETECTION_HIGH, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.EDGE_DETECTION_MEDIUM, ActivationFunctions.groundRelu()),
                new Filter(Kernel.EDGE_DETECTION_SOFT, ActivationFunctions.groundRelu()),
                new Filter(Kernel.SOBEL_EDGE_HORIZONTAL, ActivationFunctions.groundRelu()),
                new Filter(Kernel.SOBEL_EDGE_VERTICAL, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.IDENTITY, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.SHARPEN, ActivationFunctions.groundRelu()),

//                new Filter(Kernel.IDENTITY, ActivationFunctions.groundRelu()),
        };

        Filter[] filters2 = {
                new Filter(Kernel.IDENTITY, ActivationFunctions.groundRelu()),
                new Filter(Kernel.SHARPEN, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.CUSTOM_1, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.CUSTOM_2, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.CUSTOM_3, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.CUSTOM_4, ActivationFunctions.groundRelu()),
//                new Filter(Kernel.CUSTOM_5, ActivationFunctions.groundRelu()),
        };
        String inputPath = "/home/zachs/Develop/Java/artificialintelligence/datasets/input_images/scaled";
//        convolveImage(filters, filters2, new File(inputPath+"/")).printSchema();
//        CSVFileWriter csvFileWriter = new CSVFileWriter("/home/zachs/Develop/Java/artificialintelligence/datasets/dataset3.csv");
        BinaryDatasetWriter binaryDatasetWriter = new BinaryDatasetWriter("/home/zachs/Develop/Java/artificialintelligence/datasets/dataset3.b");

        ConvolutionParallelExecutor output = null;
        for (int i = 2; i <= 12; i++) {
            System.out.print("For image #" + i);
            convolveImage(
                    filters,
                    filters2,
                    new File(inputPath + "/f" + i + ".jpg"),
                    new File(inputPath+"/f"+i+"_final.jpg"),
                    binaryDatasetWriter
            );
//            output.printSchema();
//            System.exit(0);
//            output.executeParallel().get();
//            createDataset(output.getChannelsOutput(), new File(inputPath + "/f"+i+"_final.jpg"), csvFileWriter);
            System.out.println(" DONE");
        }


        try {
            binaryDatasetWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

//        outputAsImages(output.getChannelsOutput(), "/home/zachs/Develop/Java/artificialintelligence/datasets/test_images");
    }

    private static void writeToBinary() {
        CSVDatasetUtils.csvToBinary(
                new File("/home/zachs/Develop/Java/artificialintelligence/datasets/dataset1.csv"),
                new File("/home/zachs/Develop/Java/artificialintelligence/datasets/binary_dataset1.b")
        );
    }

    private static void convolveImage(Filter[] filters, Filter[] filters2, File image, File fileMaks, BinaryDatasetWriter binaryDatasetWriter) throws IOException {
//        //        ConvolutionExecutor output = ConvolutionExecutor.initialize(new HsbInput(new File("/home/zachs/Downloads/ΟΔ 5396.jpg")))
        BufferedImage mask = ImageIO.read(fileMaks);

        Iterator<ConvolutionInput> mainImageInput = new GridInput(new HsbInput(ImageIO.read(image)), 6, 6).iterator();
        Iterator<ConvolutionInput> maskInput = new GridInput(new ImageInput(mask), 6, 6).iterator();
        ConvolutionInput convolutionInput = mainImageInput.next(), maskConvolutionInput = maskInput.next();

        ConvolutionExecutor convolutionExecutor = ConvolutionParallelExecutor.initialize(convolutionInput)
                .addLayerForAllChannels(new ConvolutionLayer(filters, 1, true))
                .addLayerForAllChannels(new ConvolutionLayer(filters2, 1, true))
                .addLayerForAllChannels(new PoolLayer(PoolFunction.MAX, 2, 2))
//                    .addLayerForAllChannels(new ConvolutionLayer(filters, 10, 10))
                .addLayerForAllChannels(new FlatLayer());
        float[] features = null;

        for (; mainImageInput.hasNext() && maskInput.hasNext(); convolutionInput = mainImageInput.next(), maskConvolutionInput = maskInput.next()) {
            MatrixReader[][] channels = convolutionExecutor
                    .changeInput(convolutionInput).execute().getChannelsOutput();
            if (features == null) {
                features = new float[channels.length * channels[0][0].getColumnCount() + 36];
            }
            createDataset(channels, features, (GridInputIterator.GridBlock) maskConvolutionInput.getChannels()[0], mask, binaryDatasetWriter);
        }
    }

    private static void createDataset(
            MatrixReader[][] channels,
            float[] dataForCsv,
            GridInputIterator.GridBlock block,
            BufferedImage mask,
            BinaryDatasetWriter binaryDatasetWriter
    ) throws IOException {


//        System.out.println("Row: "+block.getRealRow()+" column: "+block.getRealColumn());

        if (featureLength == null) {
            featureLength = dataForCsv.length;
        }
        else if (dataForCsv.length != featureLength) {
            throw new RuntimeException("Not all the features have the same size first feature length: "+featureLength+" current: "+dataForCsv.length);
        }

        // load the channels
        int channelPosition = 0;
        for (MatrixReader[] channel : channels) {
            int channelLength = channel[0].getColumnCount();
            System.arraycopy(channel[0].getRow(0), 0, dataForCsv, channelPosition, channelLength);
            channelPosition += channelLength;
        }

        for (int i=0; i<block.getRowCount(); i++) {
            for (int j=0; j<block.getColumnCount(); j++) {
                int target = 1 - (mask.getRGB(block.getRealColumn() + j, block.getRealRow() + i) & 0xFF / THRESHOLD);
                dataForCsv[channelPosition++] = target;
//                int byteRgb = 255 * target;
//                plot.main.paintPixel(block.getRealRow() + i, block.getRealColumn() + j, byteRgb);

            }
        }

        binaryDatasetWriter.write(dataForCsv);
//        binaryDatasetWriter.writeLine(Arrays.stream(dataForCsv).boxed().toArray());

//        plot.main.repaint();
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
                        new File(folder + "/channel_" + i + "_filter_" + j + ".jpg")
                );
                j++;
            }
            i++;
        }
    }
}

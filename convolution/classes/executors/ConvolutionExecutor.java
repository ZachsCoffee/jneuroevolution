package executors;

import layers.Layer;
import schema.LayerSchema;
import input.ImageInput;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixSchema;
import schema.ConvolutionSchemaPrinter;

import java.util.*;

public class ConvolutionExecutor {

    public static ConvolutionExecutor initialize(ImageInput imageInput) {
        return new ConvolutionExecutor(imageInput.getChannels());
    }

    private final ArrayList<Layer>[] channelsLayers;
    protected final MatrixReader[] channels;
    protected MatrixReader[][] output;

    protected ConvolutionExecutor(MatrixReader[] channels) {
        this.channels = Objects.requireNonNull(channels);

        if (channels.length == 0) {
            throw new IllegalArgumentException("Need at least one channel!");
        }

        channelsLayers = (ArrayList<Layer>[]) new ArrayList[channels.length];
        for (int i = 0; i<channelsLayers.length; i++) {
            channelsLayers[i] = new ArrayList<>();
        }

        output = new MatrixReader[channels.length][];
    }

    public ConvolutionExecutor addLayerForAllChannels(Layer layer) {
        for (ArrayList<Layer> channel : channelsLayers) {
            channel.add(layer);
        }

        return this;
    }

    public void addLayerForChannel(Layer layer, int channelPosition) {
        channelsLayers[channelPosition].add(layer);
    }

    public void execute() {
        for (int i=0; i<channels.length; i++) {
            output[i] = computeChannel(i);
        }
    }

    public double[] flatten() {
        double[] flat = new double[flatSize()];

        int startCopyIndex = 0;
        for (MatrixReader[] channel : output) {
            for (MatrixReader filter: channel) {
                int rows = filter.getRowCount();
                int columns = filter.getColumnCount();
                for (int i=0; i<rows; i++) {
                    System.arraycopy(filter.getRow(i), 0, flat, startCopyIndex, columns);
                    startCopyIndex += columns;
                }
            }
        }

        return flat;
    }

    public int flatSize() {
        int flatSize = 0;

        for (MatrixReader[] channel : output) {
            for (MatrixReader filter: channel) {
                flatSize += filter.getColumnCount() * filter.getRowCount();
            }
        }

        return flatSize;
    }

    public MatrixReader[][] getChannelsOutput() {
        return output;
    }

    public void printSchema() {
        ConvolutionSchemaPrinter convolutionSchemaPrinter = new ConvolutionSchemaPrinter(new String[]{
                "Layer type", "Channels", "Filters", "Sample size", "Stride", "Padding", "Output"
        });

        convolutionSchemaPrinter.addRow("Initial", channels.length, "-", "-", "-", "-", "-");
        int i = 0;
        for (MatrixReader channel : channels) {
            convolutionSchemaPrinter.addRow("Channel", "-", "-", channel.getRowCount() + "x" + channel.getColumnCount(), "-", "-", "-");

            MatrixSchema[] tempLayerSchema = new MatrixSchema[] {
                    new LayerSchema(channel.getRowCount(), channel.getColumnCount())
            };

            for (Layer channelLayer : channelsLayers[i]) {
                tempLayerSchema = channelLayer.toString(tempLayerSchema, convolutionSchemaPrinter);
            }

            i++;
        }

        convolutionSchemaPrinter.print();
    }

    protected MatrixReader[] computeChannel(int channelIndex) {
        MatrixReader[] previousMatrixReader = new MatrixReader[]{channels[channelIndex]};
        for (Layer layer : channelsLayers[channelIndex]) {
            previousMatrixReader = layer.computeLayer(previousMatrixReader);
        }

        return previousMatrixReader;
    }
}

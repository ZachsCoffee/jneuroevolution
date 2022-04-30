package executors;

import layers.Layer;
import schema.LayerSchema;
import input.ConvolutionInput;
import maths.matrix.MatrixReader;
import maths.matrix.MatrixSchema;
import schema.ConvolutionSchemaPrinter;

import java.util.*;

public class ConvolutionExecutor {

    public static ConvolutionExecutor initialize(ConvolutionInput convolutionInput) {
        return new ConvolutionExecutor(convolutionInput.getChannels());
    }

    private final ArrayList<Layer>[] channelsLayers;
    protected MatrixReader[] channels;
    protected MatrixReader[][] output;

    protected ConvolutionExecutor(MatrixReader[] channels) {
        setChannels(channels);

        channelsLayers = (ArrayList<Layer>[]) new ArrayList[channels.length];
        for (int i = 0; i<channelsLayers.length; i++) {
            channelsLayers[i] = new ArrayList<>();
        }

        output = new MatrixReader[channels.length][];
    }

    private void setChannels(MatrixReader[] channels) {
        this.channels = Objects.requireNonNull(channels);

        if (channels.length == 0) {
            throw new IllegalArgumentException("Need at least one channel!");
        }
    }

    public MatrixReader[][] getChannelsOutput() {
        return output;
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

    public ConvolutionExecutor execute() {
        for (int i=0; i<channels.length; i++) {
            output[i] = computeChannel(i);
        }

        return this;
    }

    public void printSchema() {
        System.out.println("Total channels " + channels.length);

        int i = 0;
        for (MatrixReader channel : channels) {
            System.out.println("Channel #" + (i + 1));
            ConvolutionSchemaPrinter convolutionSchemaPrinter = new ConvolutionSchemaPrinter(new String[]{
                    "Layer type", "Channels", "Filters", "Sample size", "Stride", "Padding", "Output"
            });

            convolutionSchemaPrinter.addRow("Channel", "-", "-", channel.getRowsCount() + "x" + channel.getColumnsCount(), "-", "-", "-");

            MatrixSchema[] tempLayerSchema = new MatrixSchema[] {
                    new LayerSchema(channel.getRowsCount(), channel.getColumnsCount())
            };

            for (Layer channelLayer : channelsLayers[i]) {
                tempLayerSchema = channelLayer.getSchema(tempLayerSchema, convolutionSchemaPrinter);
            }

            i++;

            convolutionSchemaPrinter.print();
        }
    }

    public ConvolutionExecutor changeInput(ConvolutionInput convolutionInput) {
        setChannels(convolutionInput.getChannels());
        output = new MatrixReader[channels.length][];

        return this;
    }

    protected MatrixReader[] computeChannel(int channelIndex) {
        MatrixReader[] previousMatrixReader = new MatrixReader[]{channels[channelIndex]};
        int i = 1;
        try {
            for (Layer layer : channelsLayers[channelIndex]) {
                previousMatrixReader = layer.computeLayer(previousMatrixReader);
                i++;
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Error at channel: "+(channelIndex +1)+" and layer: "+i, ex);
        }

        return previousMatrixReader;
    }
}

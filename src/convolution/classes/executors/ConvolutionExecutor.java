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

    private final List<Layer> layers = new LinkedList<>();
    protected MatrixReader[] channels;
    protected MatrixReader[][] output;

    protected ConvolutionExecutor(MatrixReader[] channels) {
        setChannels(channels);

        output = new MatrixReader[channels.length][];
    }

    public MatrixReader[][] getChannelsOutput() {
        return output;
    }

    protected void setChannels(MatrixReader[] channels) {
        this.channels = Objects.requireNonNull(channels);

        validateChannels(channels);
    }

    public ConvolutionExecutor addLayer(Layer layer) {
        layers.add(Objects.requireNonNull(layer));

        return this;
    }

    public ConvolutionExecutor execute() {
        MatrixReader[] previousMatrixReader = channels;
        int i = 0;
        try {
            for (Layer layer : layers) {
                previousMatrixReader = layer.computeLayer(previousMatrixReader);
                i++;
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Error at layer: " + i, ex);
        }

        return this;
    }

    public void printSchema() {
        System.out.println("Total channels " + channels.length);

        ConvolutionSchemaPrinter convolutionSchemaPrinter = getConvolutionSchemaPrinter(channels[0], 0);

        MatrixSchema[] tempLayerSchema = new MatrixSchema[]{
            new LayerSchema(channels[0].getRowsCount(), channels[0].getColumnsCount())
        };

        for (Layer channelLayer : layers) {
            tempLayerSchema = channelLayer.getSchema(tempLayerSchema, convolutionSchemaPrinter);
        }


        convolutionSchemaPrinter.print();
    }

    public ConvolutionExecutor changeInput(ConvolutionInput convolutionInput) {
        setChannels(convolutionInput.getChannels());
        output = new MatrixReader[channels.length][];

        return this;
    }

    protected ConvolutionSchemaPrinter getConvolutionSchemaPrinter(MatrixReader channel, int i) {
        System.out.println("Channel #" + (i + 1));
        ConvolutionSchemaPrinter convolutionSchemaPrinter = new ConvolutionSchemaPrinter(new String[]{
            "Layer type", "Channels", "Filters", "Sample size", "Stride", "Padding", "Output"
        });

        convolutionSchemaPrinter.addRow(
            "Channel",
            "-",
            "-",
            channel.getRowsCount() + "x" + channel.getColumnsCount(),
            "-",
            "-",
            "-"
        );
        return convolutionSchemaPrinter;
    }

    private void validateChannels(MatrixReader[] channels) {
        if (channels.length == 0) {
            throw new IllegalArgumentException("Need at least one channel!");
        }

        int firstChannelRows = channels[0].getRowsCount();
        int firstChannelColumns = channels[0].getColumnsCount();

        for (int i = 1; i < channels.length; i++) {
            int channelRowCount = channels[i].getRowsCount();
            int channelColumnCount = channels[i].getColumnsCount();

            if (channelRowCount != firstChannelRows) {
                throw new IllegalArgumentException(
                    "All channels must have the same schema. First channel rows: " + firstChannelRows + " " + (i + 1) + "th channel rows: " + channelRowCount
                );
            }

            if (channelColumnCount != firstChannelColumns) {
                throw new IllegalArgumentException(
                    "All channels must have the same schema. First channel rows: " + firstChannelRows + " " + (i + 1) + "th channel rows: " + channelColumnCount
                );
            }
        }
    }
}

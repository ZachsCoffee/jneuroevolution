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

    public static ConvolutionExecutor initialize(ConvolutionInput convolutionInput, boolean squashChannels) {
        return new ConvolutionExecutor(convolutionInput.getChannels(), squashChannels);
    }

    private final boolean squashChannels;
    private final List<Layer> layers = new LinkedList<>();
    protected MatrixReader[] channels;
    protected MatrixReader[][] output;

    protected ConvolutionExecutor(MatrixReader[] channels) {
        this(channels, false);
    }

    protected ConvolutionExecutor(MatrixReader[] channels, boolean squashChannels) {
        setChannels(channels);

        this.squashChannels = squashChannels;
    }

    public MatrixReader[][] getChannelsOutput() {
        if (output == null) throw new IllegalStateException(
            "Need to execute in order to have output."
        );

        return output;
    }

    public boolean isSquashChannels() {
        return squashChannels;
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
        if (layers.isEmpty()) throw new IllegalStateException(
            "Can't execute without layers."
        );

        if (squashChannels) {
            output = new MatrixReader[][] { computeChannels(channels) };
        }
        else {
            output = new MatrixReader[channels.length][];

            int i=0;
            for (MatrixReader channel : channels) {
                output[i] = computeChannels(new MatrixReader[] { channel });
                i++;
            }
        }

        return this;
    }

    public void printSchema() {
        System.out.println("Total channels " + channels.length);

        ConvolutionSchemaPrinter convolutionSchemaPrinter = getConvolutionSchemaPrinter(channels[0]);

        MatrixSchema[] tempLayerSchema = channels;
        System.out.println("Channels #1..." + channels.length);

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

    protected ConvolutionSchemaPrinter getConvolutionSchemaPrinter(MatrixReader channel) {
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

    private MatrixReader[] computeChannels(MatrixReader[] channels) {
        MatrixReader[] previousMatrixReader = channels;
        int i = 1;
        try {
            for (Layer layer : layers) {
                previousMatrixReader = layer.computeLayer(previousMatrixReader);
                i++;
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Error at channel: "+i+" and layer: "+i, ex);
        }

        return previousMatrixReader;
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

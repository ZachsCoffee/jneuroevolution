package executors;

import layer.ConvolutionSchemaPrinter;
import layer.Layer;
import layer.MatrixReader;
import layer.MatrixSchema;
import schema.LayerSchema;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ConvolutionSplitChannelExecutor extends ConvolutionExecutor {

    private final List<Layer>[] channelsLayers;

    @SuppressWarnings("unchecked")
    protected ConvolutionSplitChannelExecutor(MatrixReader[] channels) {
        super(channels);

        channelsLayers = new List[channels.length];

        for (int i=0; i<channelsLayers.length; i++) {
            channelsLayers[i] = new LinkedList<>();
        }
    }

    public void addLayer(Layer layer, int channelPosition) {
        channelsLayers[channelPosition].add(layer);
    }

    @Override
    public ConvolutionExecutor addLayer(Layer layer) {
        for (List<Layer> channel : channelsLayers) {
            channel.add(layer);
        }

        return this;
    }

    @Override
    protected void setChannels(MatrixReader[] channels) {
        this.channels = Objects.requireNonNull(channels);

        if (channels.length == 0) {
            throw new IllegalArgumentException("Need at least one channel!");
        }
    }

    @Override
    public ConvolutionExecutor execute() {
        for (int i=0; i<channels.length; i++) {
            output[i] = computeChannel(i);
        }

        return this;
    }

    @Override
    public void printSchema() {
        System.out.println("Total channels " + channels.length);

        int i = 0;
        for (MatrixReader channel : channels) {
            System.out.println("Channel #" + (i + 1));
            ConvolutionSchemaPrinter convolutionSchemaPrinter = getConvolutionSchemaPrinter(channel);

            MatrixSchema[] tempLayerSchema = new MatrixSchema[]{
                new LayerSchema(channel.getRowsCount(), channel.getColumnsCount())
            };

            for (Layer channelLayer : channelsLayers[i]) {
                tempLayerSchema = channelLayer.getSchema(tempLayerSchema, convolutionSchemaPrinter);
            }

            i++;

            convolutionSchemaPrinter.print();
        }
    }

    protected MatrixReader[] computeChannel(int channelIndex) {
        MatrixReader[] previousMatrixReader = new MatrixReader[]{channels[channelIndex]};
        int i = 0;
        try {
            for (Layer layer : channelsLayers[channelIndex]) {
                previousMatrixReader = layer.computeLayer(previousMatrixReader);
                i++;
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Error at channel: " + channelIndex + " and layer: " + i, ex);
        }

        return previousMatrixReader;
    }
}

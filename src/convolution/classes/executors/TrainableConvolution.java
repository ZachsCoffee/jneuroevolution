package executors;

import layer.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TrainableConvolution implements executors.common.TrainableConvolution {

    private final List<TrainableLayer> layers;
    private final int totalWeights;

    public TrainableConvolution(List<TrainableLayer> layers) {
        this.layers = Objects.requireNonNull(layers);

        if (layers.isEmpty()) throw new IllegalArgumentException(
            "Need at least one layer."
        );

        int countWeights = 0;
        for (TrainableLayer layer : layers) {
            countWeights += layer.getWeightsCount();
        }

        totalWeights = countWeights;
    }

    public int getTotalWeights() {
        return totalWeights;
    }

    @Override
    public double getWeightAt(int index) {
        int[] position = getLayer(index);

        return layers.get(position[0]).getWeightAt(position[1]);
    }

    public void setWeightAt(int index, double weight) {
        int[] position = getLayer(index);

        layers.get(position[0]).setWeightAt(position[1], weight);
    }

    public MatrixReader[] execute(MatrixReader[] channels) {
        validateChannels(channels);

        MatrixReader[] previousMatrixReader = channels;
        int i = 1;
        try {
            for (Layer layer : layers) {
                previousMatrixReader = layer.computeLayer(previousMatrixReader);
                i++;
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Error at channel: " + i + " and layer: " + i, ex);
        }

        return previousMatrixReader;
    }

    public void printSchema(MatrixReader[] channels) {
        System.out.println("Total channels " + channels.length);

        ConvolutionSchemaPrinter convolutionSchemaPrinter = getConvolutionSchemaPrinter(channels[0]);
        MatrixSchema[] tempLayerSchema;

        tempLayerSchema = channels;

        System.out.println("Channels #1..." + channels.length);

        for (Layer channelLayer : layers) {
            tempLayerSchema = channelLayer.getSchema(tempLayerSchema, convolutionSchemaPrinter);
        }

        convolutionSchemaPrinter.print();
    }

    @Override
    public Iterator<TrainableLayer> iterator() {
        return layers.iterator();
    }

    @Override
    public executors.common.TrainableConvolution copy() {
        List<TrainableLayer> copiedLayers = new LinkedList<>();

        for (TrainableLayer layer : layers) {
            copiedLayers.add(layer.copy());
        }

        return new TrainableConvolution(
            copiedLayers
        );
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private int[] getLayer(int index) {
        int layerIndex = 0;
        for (TrainableLayer layer : layers) {
            if (index >= layer.getWeightsCount()) {
                index -= layer.getWeightsCount();
            }
            else {
                return new int[]{layerIndex, index};
            }
            layerIndex++;
        }

        throw new RuntimeException("Failed to find layer with index: " + index);
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

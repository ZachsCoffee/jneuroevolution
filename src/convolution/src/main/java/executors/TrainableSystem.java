package executors;

import core.layer.*;
import core.schema.SchemaRow;

import java.util.*;

public class TrainableSystem implements TrainableLayer, Iterable<Layer> {

    private final ArrayList<Layer> layers;
    private final ArrayList<TrainableLayer> trainableLayers = new ArrayList<>();
    private final int totalWeights;
    private final int[] layerPosition = new int[2];

    public TrainableSystem(ArrayList<Layer> layers) {
        this.layers = Objects.requireNonNull(layers);

        if (layers.isEmpty()) throw new IllegalArgumentException(
            "Need at least one core.layer."
        );

        int countWeights = 0;
        boolean hasTrainableLayer = false;
        for (Layer layer : layers) {
            if (layer instanceof TrainableLayer) {
                TrainableLayer tempTrainableLayer = (TrainableLayer) layer;
                trainableLayers.add(tempTrainableLayer);
                countWeights += tempTrainableLayer.getTotalWeights();
                hasTrainableLayer = true;
            }
        }

        if (!hasTrainableLayer) throw new IllegalArgumentException(
            "Need at least one trainable layer."
        );

        totalWeights = countWeights;
    }

    public int getTotalWeights() {
        return totalWeights;
    }

    @Override
    public double getWeightAt(int index) {
        int[] position = getLayer(index);

        return trainableLayers.get(position[0]).getWeightAt(position[1]);
    }

    @Override
    public int getOutputChannelsCount() {
        Layer layer = layers.get(trainableLayers.size() - 1);

        if (!(layer instanceof TrainableLayer)) throw new IllegalStateException(
            "The last layer isn't trainable."
        );

        return ((TrainableLayer)layer).getOutputChannelsCount();
    }

    public void setWeightAt(int index, double weight) {
        int[] position = getLayer(index);

        trainableLayers.get(position[0]).setWeightAt(position[1], weight);
    }

    public MatrixReader[] execute(MatrixReader[] channels) {
        validateChannels(channels);

        MatrixReader[] previousMatrixReader = channels;
        int i = 1;
        try {
            for (Layer layer : layers) {
                previousMatrixReader = layer.execute(previousMatrixReader);
                i++;
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Error at channel: " + i + " and core.layer: " + i, ex);
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
            convolutionSchemaPrinter.addRow(channelLayer.getSchemaRow(tempLayerSchema));
            tempLayerSchema = channelLayer.getSchema(tempLayerSchema);
        }

        convolutionSchemaPrinter.print();
    }

    @Override
    public Iterator<Layer> iterator() {
        return layers.iterator();
    }

    @Override
    public TrainableLayer copy() {
        ArrayList<Layer> copiedLayers = new ArrayList<>(layers.size());

        for (Layer layer : layers) {
            copiedLayers.add(layer.copy());
        }

        return new TrainableSystem(
            copiedLayers
        );
    }

    @Override
    public MatrixSchema[] getSchema(MatrixSchema[] inputSchema) {
        return new MatrixSchema[0];
    }

    @Override
    public SchemaRow getSchemaRow(MatrixSchema[] inputSchema) {
        return null;
    }

    protected ConvolutionSchemaPrinter getConvolutionSchemaPrinter(MatrixReader channel) {
        ConvolutionSchemaPrinter convolutionSchemaPrinter = new ConvolutionSchemaPrinter(new String[]{
            "Layer type", "Channels", "Filters", "Sample size", "Stride", "Padding", "Trainable weights", "Output"
        });

        convolutionSchemaPrinter.addRow(
            "Channel",
            "-",
            "-",
            channel.getRowsCount() + "x" + channel.getColumnsCount(),
            "-",
            "-",
            "-",
            "-"
        );
        return convolutionSchemaPrinter;
    }

    private int[] getLayer(int index) {
        int layerIndex = 0;
        for (TrainableLayer trainableLayer : trainableLayers) {
            if (index >= trainableLayer.getTotalWeights()) {
                index -= trainableLayer.getTotalWeights();
            }
            else {
                layerPosition[0] = layerIndex;
                layerPosition[1] = index;
                return layerPosition;
            }
            layerIndex++;
        }

        throw new RuntimeException("Failed to find core.layer with index: " + index);
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
                    "All channels must have the same core.schema. First channel rows: " + firstChannelRows + " " + (i + 1) + "th channel rows: " + channelRowCount
                );
            }

            if (channelColumnCount != firstChannelColumns) {
                throw new IllegalArgumentException(
                    "All channels must have the same core.schema. First channel rows: " + firstChannelRows + " " + (i + 1) + "th channel rows: " + channelColumnCount
                );
            }
        }
    }
}

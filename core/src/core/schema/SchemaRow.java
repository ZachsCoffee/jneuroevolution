package core.schema;

import java.util.Objects;

public class SchemaRow {

    private String layerType;
    private Integer channelsCount = null;
    private Integer filtersCount = null;
    private Integer sampleSize = null;
    private Integer stride = null;
    private Integer padding = null;
    private int output;

    public SchemaRow setLayerType(String layerType) {
        this.layerType = layerType;
        return this;
    }

    public SchemaRow setChannelsCount(Integer channelsCount) {
        this.channelsCount = channelsCount;
        return this;
    }

    public SchemaRow setFiltersCount(Integer filtersCount) {
        this.filtersCount = filtersCount;
        return this;
    }

    public SchemaRow setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
        return this;
    }

    public SchemaRow setStride(Integer stride) {
        this.stride = stride;
        return this;
    }

    public SchemaRow setPadding(Integer padding) {
        this.padding = padding;
        return this;
    }

    public SchemaRow setOutput(int output) {
        this.output = output;
        return this;
    }

    public String[] toRow() {
        return new String[] {
            Objects.requireNonNull(layerType),
            format(channelsCount),
            format(filtersCount),
            format(sampleSize),
            format(stride),
            format(padding),
            format(output),
        };
    }

    private String format(Object value) {
        return value == null
            ? "-"
            : value.toString();
    }
}

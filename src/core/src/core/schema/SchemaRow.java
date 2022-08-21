package core.schema;

import java.util.Objects;

public class SchemaRow {

    private String layerType;
    private Integer channelsCount = null;
    private Integer filtersCount = null;
    private Integer sampleSize = null;
    private String stride = null;
    private String padding = null;
    private String output;

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

    public SchemaRow setStride(String stride) {
        this.stride = stride;
        return this;
    }

    public SchemaRow setPadding(String padding) {
        this.padding = padding;
        return this;
    }

    public SchemaRow setOutput(String output) {
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
            Objects.requireNonNull(output),
        };
    }

    private String format(Object value) {
        return value == null
            ? "-"
            : value.toString();
    }
}

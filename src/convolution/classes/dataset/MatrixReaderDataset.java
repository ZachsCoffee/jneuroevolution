package dataset;

import data_manipulation.DatasetTarget;
import core.layer.MatrixReader;

public class MatrixReaderDataset implements DatasetTarget {

    private final int dataLength;
    private final MatrixReader[][] data;
    private final double[][] targets;

    public MatrixReaderDataset(MatrixReader[][] data, double[][] targets) {
        this.data = data;
        this.targets = targets;
        dataLength = this.data.length;
    }

    public MatrixReader[][] getData() {
        return data;
    }

    @Override
    public double[][] getTargets() {
        return targets;
    }

    public int getDataLength() {
        return dataLength;
    }

    private void validate(MatrixReader[][] data, double[][] targets) {
        if (data.length != targets.length) throw new IllegalArgumentException(
            "Data length isn't equal to target length. Data length: " + data.length +" Target length: " + targets.length
        );
    }
}

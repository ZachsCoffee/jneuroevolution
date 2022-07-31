package dataset;

import data_manipulation.DatasetTarget;
import layer.MatrixReader;

public class MatrixReaderDataset implements DatasetTarget {

    private MatrixReader[][] data;
    private double[][] targets;

    public MatrixReaderDataset(MatrixReader[][] data, double[][] targets) {
        this.data = data;
        this.targets = targets;
    }

    public MatrixReader[][] getData() {
        return data;
    }

    @Override
    public double[][] getTargets() {
        return targets;
    }

    private void validate(MatrixReader[][] data, double[][] targets) {
        if (data.length != targets.length) throw new IllegalArgumentException(
            "Data length isn't equal to target length. Data length: " + data.length +" Target length: " + targets.length
        );
    }
}

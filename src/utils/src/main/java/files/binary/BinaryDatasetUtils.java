package files.binary;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class BinaryDatasetUtils {
    public static double[][] loadFrom(File binaryFile) {
        List<double[]> data = new LinkedList<>();

        try (BinaryDatasetReader binaryDatasetReader = new BinaryDatasetReader(binaryFile)) {
            double[] feature;
            while ((feature = binaryDatasetReader.read()) != null) {
                data.add(feature);
            }

            return data.toArray(new double[0][]);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to read from file "+binaryFile, e);
        }
    }
}

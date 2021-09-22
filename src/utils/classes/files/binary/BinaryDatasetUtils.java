package files.binary;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class BinaryDatasetUtils {
    public static float[][] loadFrom(File binaryFile) {
        List<float[]> data = new LinkedList<>();

        try (BinaryDatasetReader binaryDatasetReader = new BinaryDatasetReader(binaryFile)) {
            float[] feature;
//            while ((feature = binaryDatasetReader.read()) != null) {
//                data.add(feature);
//            }

            return data.toArray(new float[0][]);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to read from file "+binaryFile, e);
        }
    }
}

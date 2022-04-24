package files.csv;

import files.binary.BinaryDatasetWriter;

import java.io.File;

public class CSVDatasetUtils {
    public static void csvToBinary(File csvDataset, File binaryOutput) {
        double[][] data = CSVFileReader.readNumbersFile(csvDataset, ",");

        try (BinaryDatasetWriter binaryDatasetWriter = new BinaryDatasetWriter(binaryOutput)) {
            for (int i = 0; i < data.length; i++) {
                binaryDatasetWriter.write(data[i]);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to write to output file "+binaryOutput, e);
        }
    }
}

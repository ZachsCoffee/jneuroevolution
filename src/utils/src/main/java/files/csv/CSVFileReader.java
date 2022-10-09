/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author zachs
 */
public class CSVFileReader {

    private CSVFileReader() {
    }

    public static String[][] readStrings(File csvFile, String delimiter, DataParser dataParser) {
        if (delimiter == null || delimiter.equals("")) throw new IllegalArgumentException(
            "argument delimiter not null and at least one character"
        );
        if (dataParser == null) throw new IllegalArgumentException(
            "argument dataParser not null"
        );


        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                Files.newInputStream(csvFile.toPath()),
                StandardCharsets.UTF_8
            ))
        ) {

            ArrayList<String[]> data = new ArrayList<>();

            String[] splitedLine;
            String line;
            while ((line = reader.readLine()) != null) {
                splitedLine = line.split(delimiter);

                dataParser.dataParser(splitedLine);

                data.add(splitedLine);
            }

            return data.toArray(new String[][]{});
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static double[][] readNumbersFile(File csvFile, String delimiter, DataParser dataParser) {
        return readNumbersFile(csvFile.getPath(), delimiter, dataParser);
    }

    public static double[][] readNumbersFile(String csvFilePath, String delimiter, DataParser dataParser) {

        if (delimiter == null || delimiter.equals("")) throw new IllegalArgumentException(
            "argument delimiter not null and at least one character"
        );
        if (dataParser == null) throw new IllegalArgumentException(
            "argument dataParser not null"
        );

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                Files.newInputStream(Paths.get(csvFilePath)),
                StandardCharsets.UTF_8
            ))
        ) {

            ArrayList<double[]> data = new ArrayList<>();

            String[] splitedLine;
            double[] doubleArray;
            String line;
            while ((line = reader.readLine()) != null) {
                splitedLine = line.split(delimiter);
                doubleArray = new double[splitedLine.length];

                for (int i = 0; i < splitedLine.length; i++) {
                    doubleArray[i] = Double.parseDouble(splitedLine[i]);
                }

                dataParser.dataParser(doubleArray);

                data.add(doubleArray);
            }

            return data.toArray(new double[][]{});
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static double[][] readNumbersFile(File csvFile, String delimiter) {
        return readNumbersFile(csvFile.getPath(), delimiter);
    }


    /**
     * Read a csv file of numbers ONLY. The character set must be UTF-8.
     *
     * @param csvFilePath The path to csv file
     * @param delimiter   The column delimiter (delimiter) of csv file.
     * @return The data from the file
     */
    public static double[][] readNumbersFile(String csvFilePath, String delimiter) {

        if (delimiter == null || delimiter.equals("")) throw new IllegalArgumentException(
            "argument delimiter not null and at least one character"
        );

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                Files.newInputStream(Paths.get(csvFilePath)),
                StandardCharsets.UTF_8
            ))
        ) {

            ArrayList<double[]> data = new ArrayList<>();

            String[] splitedLine;
            double[] doubleArray;
            String line;
            while ((line = reader.readLine()) != null) {
                splitedLine = line.split(delimiter);
                doubleArray = new double[splitedLine.length];

                for (int i = 0; i < splitedLine.length; i++) {
                    doubleArray[i] = Double.parseDouble(splitedLine[i]);
                }

                data.add(doubleArray);
            }

            return data.toArray(new double[][]{});
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public interface DataParser {

        default void dataParser(double[] csvData) {
        }

        ;

        default void dataParser(String[] csvData) {
        }

        ;
    }
}

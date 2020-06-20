/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author zachs
 */
public class CSVFileReader {
    
    private CSVFileReader(){}
    
    public static double[][] readNumbersFile(File csvFile, String separator, DataParser dataParser) {
        
        if (separator == null || separator.equals("")) throw new IllegalArgumentException(
                "arguments separetor not null and at least one character"
        );
        if (dataParser == null) throw new IllegalArgumentException(
                "arguments dataParser not null"
        );
        
        return readNumbersFile(csvFile.getPath(), separator, dataParser);
    }
    
    public static double[][] readNumbersFile(String csvFilePath, String separator, DataParser dataParser) {
        
        if (separator == null || separator.equals("")) throw new IllegalArgumentException(
                "arguments separetor not null and at least one character"
        );
        if (dataParser == null) throw new IllegalArgumentException(
                "arguments dataParser not null"
        );
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilePath), "UTF-8"))){
            
            ArrayList<double[]> data = new ArrayList();
            
            String[] splitedLine;
            double[] doubleArray;
            String line;
            while ((line = reader.readLine()) != null){
                splitedLine = line.split(separator);
                doubleArray = new double[splitedLine.length];
                
                for (int i=0; i<splitedLine.length; i++){
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
    
    public static double[][] readNumbersFile(File csvFile, String separator){
        
        if (separator == null || separator.equals("")) throw new IllegalArgumentException(
                "arguments separetor not null and at least one character"
        );
        
        return readNumbersFile(csvFile.getPath(), separator);
    }
    
    
    
    /**
     * Read a csv file of numbers ONLY. The character set must be UTF-8. 
     * @param csvFilePath The path to csv file
     * @param separator The column separator (delimiter) of csv file. 
     * @return The data from the file
     */
    public static double[][] readNumbersFile(String csvFilePath, String separator){
        
        if (separator == null || separator.equals("")) throw new IllegalArgumentException(
                "arguments separetor not null and at least one character"
        );
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilePath), "UTF-8"))){
            
            ArrayList<double[]> data = new ArrayList();
            
            String[] splitedLine;
            double[] doubleArray;
            String line;
            while ((line = reader.readLine()) != null){
                splitedLine = line.split(separator);
                doubleArray = new double[splitedLine.length];
                
                for (int i=0; i<splitedLine.length; i++){
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
        
        public void dataParser(double[] csvData);
    }
}

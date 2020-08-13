/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Zachs
 */
public class CSVFileWriter implements AutoCloseable {
    
    private final String DEFAULT_DELIMITER = ",";
    
    private final String DELIMITER, NEXT_LINE = "\n";
    
    private File csvFile;
    private FileWriter fileWriter;
    private String stringBuffer = "";
    
    public CSVFileWriter(String filePath){
        
        this(new File(filePath));
    }
    public CSVFileWriter(String filePath, String delimiter){
        
        this(new File(filePath), delimiter);
    }
    public CSVFileWriter(File csvFile){
        open(csvFile);
        
        this.csvFile = csvFile;
        
        DELIMITER = DEFAULT_DELIMITER;
    }
    public CSVFileWriter(File csvFile, String delimiter){
        open(csvFile);
        
        this.csvFile = csvFile;
        
        DELIMITER = delimiter;
    }
    
    public String getBuffer(){
        return stringBuffer;
    }
    public File getCsvFile(){
        return csvFile;
    }
    
    public void clearBuffer(){
        stringBuffer = "";
    }
    
    public final void open(File csvFile){
        if (fileWriter == null){
            try {
                fileWriter = new FileWriter(csvFile);
            } 
            catch (IOException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }
    
    public void addToBuffer(Object... object){
        if (stringBuffer.equals("")){
            stringBuffer += join(object);        
        }
        else{
            stringBuffer += DELIMITER+join(object);
        }
    }
    
    public void writeLine(){
        try {
            fileWriter.write(stringBuffer+NEXT_LINE);
            fileWriter.flush();
        } 
        catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        
        stringBuffer = "";
    }
    
    public void writeLine(Object... objects){
        try {
            if (stringBuffer.equals("")){
                fileWriter.write(join(objects)+NEXT_LINE);
            }
            else{
                fileWriter.write(stringBuffer+DELIMITER+join(objects)+NEXT_LINE);
            }
            
            fileWriter.flush();
        } 
        catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        
        stringBuffer = "";
    }
    
    @Override
    public void close(){
        try {
            fileWriter.flush();
            fileWriter.close();
        } 
        catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    private String join(Object[] objects){
        String joinedString = "";
        
        int i=0;
        for (; i<objects.length -1; i++){
            joinedString += objects[i].toString().replace("\n", "")+DELIMITER;
        }
        joinedString += objects[i].toString().replace("\n", "");
        
        return joinedString;
    }
}

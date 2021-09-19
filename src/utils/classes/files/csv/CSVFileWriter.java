/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files.csv;

import java.io.*;
import java.util.Iterator;

/**
 *
 * @author Zachs
 */
public class CSVFileWriter implements AutoCloseable {

    private final String DEFAULT_DELIMITER = ",";

    private final String DELIMITER, NEXT_LINE = "\n";

    private File csvFile;
    private PrintWriter printWriter;
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
        if (printWriter == null){
            try {
                printWriter = new PrintWriter(new BufferedWriter(new FileWriter(csvFile)));
            }
            catch (IOException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    public void addToBuffer(Object... objects){
        if (stringBuffer.equals("")){
            stringBuffer += join(objects);
        }
        else{
            stringBuffer += DELIMITER+join(objects);
        }
    }

    public void writeLine(){
        printWriter.write(stringBuffer+NEXT_LINE);

        stringBuffer = "";
    }

    public void writeLine(Object... objects) {
        if (stringBuffer.equals("")){
            printWriter.write(join(objects)+NEXT_LINE);
        }
        else{
            printWriter.write(stringBuffer+DELIMITER+join(objects)+NEXT_LINE);
        }
        printWriter.flush();
        stringBuffer = "";
    }

    @Override
    public void close() {
            printWriter.flush();
            printWriter.close();
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

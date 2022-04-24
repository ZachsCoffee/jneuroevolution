package files.binary;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public class BinaryDatasetWriter implements AutoCloseable {
    private final DataOutputStream outputStream;
    private DoubleBuffer doubleBuffer;
    private ByteBuffer byteBuffer;
    private boolean writeHeader = true;

    public BinaryDatasetWriter(String outputFile) {
        this(new File(outputFile));
    }

    public BinaryDatasetWriter(File outputFile) {
        try {
            outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to open output stream for file: "+outputFile, e);
        }
    }

    public void write(Object... data) throws IOException {
        double[] array = new double[data.length];

        for (int i=0; i<data.length; i++) {
            array[i] = Double.parseDouble(data[0].toString());
        }

        write(array);
    }

    public void write(double... data) throws IOException {
        if (writeHeader) {
            outputStream.writeInt(data.length);

            byteBuffer = ByteBuffer.allocate(data.length * 8);
            doubleBuffer = byteBuffer.asDoubleBuffer();

            writeHeader = false;
        }

        doubleBuffer.clear();
        doubleBuffer.put(data);

        outputStream.write(byteBuffer.array());
    }

    @Override
    public void close() throws Exception {
        outputStream.flush();
        outputStream.close();
    }
}

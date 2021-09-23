package files.binary;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class BinaryDatasetWriter implements AutoCloseable {
    private final DataOutputStream outputStream;
    private FloatBuffer doubleBuffer;
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
        float[] array = new float[data.length];

        for (int i=0; i<data.length; i++) {
            array[i] = Float.parseFloat(data[0].toString());
        }

        write(array);
    }

    public void write(float... data) throws IOException {
        if (writeHeader) {
            outputStream.writeInt(data.length);

            byteBuffer = ByteBuffer.allocate(data.length * 4);
            doubleBuffer = byteBuffer.asFloatBuffer();

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

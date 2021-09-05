package files.binary;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public class BinaryDatasetReader implements AutoCloseable {
    private final DataInputStream dataInputStream;
    private boolean readHeader = true;
    private DoubleBuffer doubleBuffer;
    private ByteBuffer byteBuffer;
    private byte[] buffer;

    public BinaryDatasetReader(String inputFile) {
        this(new File(inputFile));
    }

    public BinaryDatasetReader(File inputFile) {
        try {
            dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to open input stream to file: "+inputFile);
        }
    }

    public double[] read() throws IOException {
        if (readHeader) {
            // read the first int that have the size of each feature in doubles
            int bufferSize = dataInputStream.readInt() * 8;
            buffer = new byte[bufferSize];

            byteBuffer = ByteBuffer.allocate(bufferSize);
            doubleBuffer = byteBuffer.asDoubleBuffer();

            readHeader = false;
        }

        int readCount = dataInputStream.read(buffer);

        if (readCount == buffer.length) {
            byteBuffer.put(buffer);
            return doubleBuffer.array();
        }
        else if (readCount == -1) {
            return null;
        }
        else {
            throw new RuntimeException("Broken file tried to read "+buffer.length+" bytes and found only "+readCount+" bytes");
        }
    }

    @Override
    public void close() throws Exception {
        dataInputStream.close();
        byteBuffer = null;
        doubleBuffer = null;
        buffer = null;
    }
}

package files.binary;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class BinaryDatasetReader implements AutoCloseable {
    private final DataInputStream dataInputStream;
    private final BufferedInputStream bufferedInputStream;
    private boolean readHeader = true;
    private FloatBuffer floatBuffer;
    private ByteBuffer byteBuffer;
    private byte[] tempByteBuffer;
    private int featureSize;

    public BinaryDatasetReader(String inputFile) {
        this(new File(inputFile));
    }

    public BinaryDatasetReader(File inputFile) {
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(inputFile));
            dataInputStream = new DataInputStream(bufferedInputStream);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to open input stream to file: "+inputFile);
        }
//        catch (IOException e) {
//            throw new RuntimeException("Failed to get the file descriptor for random access"+inputFile);
//        }
    }

    public float[] read() throws IOException {
        if (readHeader) {
            // read the first int that have the size of each feature in doubles
            featureSize = dataInputStream.readInt();
            int bufferSize = featureSize * 4;
            tempByteBuffer = new byte[bufferSize];

            byteBuffer = ByteBuffer.allocate(bufferSize);
            floatBuffer = byteBuffer.asFloatBuffer();

            readHeader = false;
        }

        int readCount = dataInputStream.read(tempByteBuffer);

        if (readCount == tempByteBuffer.length) {
            byteBuffer.clear();
            floatBuffer.clear();

            byteBuffer.put(tempByteBuffer);

            float[] tempDoubleBuffer = new float[featureSize];
            floatBuffer.get(tempDoubleBuffer);

            return tempDoubleBuffer;
        }
        else if (readCount == -1) {
            return null;
        }
        else {
            throw new RuntimeException("Broken file tried to read "+ tempByteBuffer.length+" bytes and found only "+readCount+" bytes");
        }
    }

    @Override
    public void close() throws Exception {
        dataInputStream.close();
        byteBuffer = null;
        floatBuffer = null;
        tempByteBuffer = null;
    }
}

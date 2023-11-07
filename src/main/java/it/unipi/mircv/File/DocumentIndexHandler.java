package it.unipi.mircv.File;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static it.unipi.mircv.Index.Config.*;

public class DocumentIndexHandler{
    private final FileChannel fileChannel;
    private final RandomAccessFile randomAccessFile;
    long currentPosition;
    String filepath = "data/documentIndex.dat";

    public DocumentIndexHandler() throws IOException {
        File file = new File(filepath);
        if (file.exists()) {
            System.out.println("Document Index file founded");
        } else {
            // Create the file
            if (file.createNewFile()) {
                System.out.println("Document Index file created correctly");
            } else {
                System.out.println("Failed to create Document Index file");
            }
        }

        randomAccessFile = new RandomAccessFile(filepath,"rw");
        fileChannel = randomAccessFile.getChannel();
        currentPosition = AVGDOCLENGHT_BYTES_LENGTH + NUM_DOC_BYTES_LENGTH;
    }

    public void writeEntry(String docNo, int docLength) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DOCNO_BYTES_LENGTH + DOCLENGTH_BYTES_LENGTH);
        byteBuffer.put(docNo.getBytes());
        byteBuffer.position(DOCNO_BYTES_LENGTH);
        byteBuffer.putInt(docLength);

        byteBuffer.rewind();
        fileChannel.position(currentPosition);
        fileChannel.write(byteBuffer);
        currentPosition += (DOCNO_BYTES_LENGTH + DOCLENGTH_BYTES_LENGTH);
    }

    public void writeAverageDocumentLength(float averageDocumentLength, int numberOfDocuments) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(AVGDOCLENGHT_BYTES_LENGTH + NUM_DOC_BYTES_LENGTH);
        byteBuffer.putFloat(averageDocumentLength);
        byteBuffer.putInt(numberOfDocuments);
        fileChannel.position(0);
        fileChannel.write(byteBuffer);
    }

    public int readDocumentLength(int docId) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(DOCLENGTH_BYTES_LENGTH);
        fileChannel.position(AVGDOCLENGHT_BYTES_LENGTH + NUM_DOC_BYTES_LENGTH + (long) docId * (DOCNO_BYTES_LENGTH + DOCLENGTH_BYTES_LENGTH) + DOCNO_BYTES_LENGTH);
        fileChannel.read(buffer);
        buffer.position(0);
        return buffer.getInt();
    }
    public void closeFileChannel() throws IOException {
        randomAccessFile.close();
        fileChannel.close();
    }
}
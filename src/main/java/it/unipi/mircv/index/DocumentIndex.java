package it.unipi.mircv.index;
import it.unipi.mircv.file.DocumentIndexFileHandler;

import java.io.IOException;


public class DocumentIndex {
    //private final ArrayList<Integer> documentLengths = new ArrayList<>();
    //private final ArrayList<String> documentNos = new ArrayList<>();
    private int numberOfDocuments;
    private final DocumentIndexFileHandler documentIndexFileHandler;
    private long numberOfTokens;

    public DocumentIndex() throws IOException {
        documentIndexFileHandler = new DocumentIndexFileHandler();
        this.numberOfDocuments = 0;
    }
    public DocumentIndex(String filePath) throws IOException {
        documentIndexFileHandler = new DocumentIndexFileHandler(filePath);
        this.numberOfDocuments = 0;
    }

    public void add(String docNo, int docLength) throws IOException {
        documentIndexFileHandler.writeEntry(docNo, docLength);
        numberOfDocuments ++;
        numberOfTokens += docLength;
    }

    public void addAverageDocumentLength() throws IOException {
        documentIndexFileHandler.writeAverageDocumentLength(numberOfTokens / (float) numberOfDocuments, numberOfDocuments);
        documentIndexFileHandler.closeFileChannel();
    }
    public int getNumDocs(){
        return numberOfDocuments;
    }

}

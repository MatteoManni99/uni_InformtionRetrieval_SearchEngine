package it.unipi.mircv.indexing;
import java.util.ArrayList;


public class DocumentIndex {
    private final ArrayList<Integer> documentLengths = new ArrayList<>();
    private Float averageDocumentLength = null;

    public void add(int docLength){
        documentLengths.add(docLength);
    }
    public int get(int docId){
        return documentLengths.get(docId);
    }

    //to be performed ones
    public void computeAverageDocumentLength(){
        int collectionLen = 0;
        for (Integer docLen : documentLengths) {
            collectionLen += docLen;
        }
        averageDocumentLength = ((float) collectionLen/documentLengths.size());
    }

    public Float getAverageDocumentLength(){
        return averageDocumentLength;
    }

}

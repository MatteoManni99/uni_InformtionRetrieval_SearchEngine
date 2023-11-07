package it.unipi.mircv.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;


import static it.unipi.mircv.Index.Config.*;

public class LexiconHandler{
    //Class that create a file-channel to the lexicon file and implement write and read method for that file

    private boolean binarySearchDone = false;
    private FileChannel lexiconFile;

    private String name = "lexicon.dat";
    private String path = "data/";
    public LexiconHandler() throws FileNotFoundException{
        RandomAccessFile raf = new RandomAccessFile(this.path + this.name, "rw");
        this.lexiconFile = raf.getChannel();
    }

    public LexiconHandler(String name) throws FileNotFoundException{
        RandomAccessFile raf = new RandomAccessFile(this.path + name, "rw");
        this.lexiconFile = raf.getChannel();
    }

    public ByteBuffer findTermEntry(String term) throws IOException {
        //Find a term in the lexicon file by binary search assuming that a=0; b=FileSize; c = center that we calculate at each iteration
        for(int i=term.length();i<TERM_BYTES_LENGTH;i++){      //ADD BLANKSPACE TO THE STRING
            term = term.concat("\0");
        }

        ByteBuffer dataBuffer = ByteBuffer.allocate(LEXICON_ENTRY_LENGTH);


        ByteBuffer termBuffer = ByteBuffer.allocate(TERM_BYTES_LENGTH);

        long fileSize = lexiconFile.size();  // size
        System.out.println("File size:"+fileSize); //DEBUG

        long left = 0;
        long numTerm = (fileSize/LEXICON_ENTRY_LENGTH);
        long right = numTerm-1;
        //calculate the center using the file size


        //take the center element.


         while(left<=right){  //search another term if not found
             long center = (right+left)/2;
             lexiconFile.read(termBuffer, center *LEXICON_ENTRY_LENGTH);
             String centerTerm = new String(termBuffer.array(), StandardCharsets.UTF_8);

             if(centerTerm.compareTo(term)<0){

                 left = center+1;  //move the left bound to centerRow

             }
             else if (centerTerm.compareTo(term)>0){

                 right = center-1;   //move the right bound to centerRow

             }
             else{

                 lexiconFile.read(dataBuffer, center *LEXICON_ENTRY_LENGTH);
                 return dataBuffer;
             }
             termBuffer.clear();
         }

         System.out.println(); //DEBUG
         return dataBuffer;
    }

    public int getCf(ByteBuffer dataBuffer) throws IOException {
        int cf = 0;
        dataBuffer.position(TERM_BYTES_LENGTH+OFFSET_BYTES_LENGTH+DOCUMFREQ_BYTES_LENGTH);
        cf = dataBuffer.getInt();
        return cf;
    }
    public int getDf(ByteBuffer dataBuffer) throws IOException {
        int df=0;
        dataBuffer.position(TERM_BYTES_LENGTH+OFFSET_BYTES_LENGTH);
        df = dataBuffer.getInt();
        return df;
    }
    public int getOffset(ByteBuffer dataBuffer) throws IOException {
        int offset = 0;
        dataBuffer.position(TERM_BYTES_LENGTH);
        offset = dataBuffer.getInt();
        return offset;
    }
}

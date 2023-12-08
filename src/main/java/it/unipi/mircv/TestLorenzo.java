package it.unipi.mircv;

import it.unipi.mircv.File.DocumentIndexHandler;
import it.unipi.mircv.File.InvertedIndexHandler;
import it.unipi.mircv.File.LexiconHandler;
import it.unipi.mircv.Index.BlockMerger;
import it.unipi.mircv.Index.Index;
import it.unipi.mircv.Index.PostingListBlock;
import it.unipi.mircv.Query.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

import static it.unipi.mircv.Config.*;
import static it.unipi.mircv.Utils.removeStopWords;
import static java.util.Collections.binarySearch;

public class TestLorenzo {
    public static void main(String[] args) throws IOException {
        flagCompressedReading = false;
        flagStemming = false;
        flagStopWordRemoval = true;

        //testCompressedReading();
        String forLexiconTest = "";
        //checkLexiconEntry(forLexiconTest);

        DocumentIndexHandler documentIndexHandler = new DocumentIndexHandler();
        Utils.loadStopWordList();
        Config.collectionSize = documentIndexHandler.readCollectionSize();
        Config.avgDocLen = documentIndexHandler.readAvgDocLen();

        //String test = "\0\0\0\0\0pfdvefvegr";
        //if (test.startsWith("\0\0\0\0"))
          //  System.out.println("deh");

        System.out.println("-----------------------------------------------------------");

        //docLenCache.put(1,50);
        //System.out.println(docLenCache.get(1));


        //testNewDisjunctive("");
        //testOldDisjunctive("");
        //testNoPriorityQueueDisjunctive("");
        //testMaxScoreDisjunctive("");
        //testConjunctive("diet detox");
        //testMaxScore("diet detox");


        //testNewConjunctive("railroad workers");
        //testNewConjunctive("10 100 1000");
        //testNoPriorityQueueDisjunctive("diet 100");

        //InvertedIndexHandler invertedIndexHandler = new InvertedIndexHandler();
        //System.out.println(invertedIndexHandler.getPostingList(0,20));
        //checkLexiconEntry("railroad");
        checkLexiconEntry("workers");
        //checkLexiconEntry("dziena");

        //testNoPriorityQueueDisjunctive("what is the distance between flat rock michigan and detroit");

        System.out.println("***************************************************************************************************");

        //testMaxScoreDisjunctive("what is the distance between flat rock michigan and detroit");
    }

    public static void testNewConjunctive(String string) throws IOException {
        long startTime = System.currentTimeMillis();
        String[] queryTerms = string.split(" ");
        queryTerms = removeStopWords(queryTerms);
        ConjunctiveDAAT newConjunctiveDAAT = new ConjunctiveDAAT(queryTerms);
        ArrayList<Integer> results = newConjunctiveDAAT.processQuery();
        System.out.println(results);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("NEW-CONJUNCTIVE finished in " + (float)elapsedTime/1000 +"sec");
    }

    public static void testConjunctiveCache(String string,LRUCache lruCache) throws IOException {
        long startTime = System.currentTimeMillis();
        String[] queryTerms = string.split(" ");
        queryTerms = removeStopWords(queryTerms);
        ConjunctiveDAATCache conjunctiveDAATCache = new ConjunctiveDAATCache(queryTerms,lruCache);
        ArrayList<Integer> results = conjunctiveDAATCache.processQuery();
        System.out.println(results);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("CONJUNCTIVE-CACHE finished in " + (float)elapsedTime/1000 +"sec");
    }

    public static void testConjunctive(String string) throws IOException {
        long startTime = System.currentTimeMillis();
        String[] queryTerms = string.split(" ");
        queryTerms = removeStopWords(queryTerms);
        ConjunctiveDAAT conjunctiveDAAT = new ConjunctiveDAAT(queryTerms);
        ArrayList<Integer> results = conjunctiveDAAT.processQuery();
        System.out.println(results);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("CONJUNCTIVE-WITHOUT-CACHE finished in " + (float)elapsedTime/1000 +"sec");
    }

    public static void testMaxScore(String string) throws IOException {
        long startTime = System.currentTimeMillis();
        String[] queryTerms = string.split(" ");
        queryTerms = removeStopWords(queryTerms);
        MaxScore maxScore2 = new MaxScore(queryTerms);
        ArrayList<Integer> results2 = maxScore2.computeMaxScore();
        System.out.println(results2);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("MAX-SCORE finished in " + (float)elapsedTime/1000 +"sec");
    }

    public static void testMaxScoreDisjunctive(String string) throws IOException {
        long startTime = System.currentTimeMillis();
        String[] queryTerms = string.split(" ");
        queryTerms = removeStopWords(queryTerms);
        MaxScoreDisjunctive maxScore = new MaxScoreDisjunctive(queryTerms);
        ArrayList<Integer> results = maxScore.computeMaxScore();
        System.out.println(results);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("MAX-SCORE finished in " + (float)elapsedTime/1000 +"sec");
    }

    public static void testOldDisjunctive(String string) throws IOException {
        long startTime = System.currentTimeMillis();
        String[] queryTerms = string.split(" ");
        queryTerms = removeStopWords(queryTerms);
        oldDisjunctive oldDisjunctive = new oldDisjunctive(queryTerms);
        ArrayList<Integer> results = oldDisjunctive.processQuery();
        System.out.println(results);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("OLD-DISJUNCTIVE finished in " + (float)elapsedTime/1000 +"sec");
    }

    public static void testNewDisjunctive(String string) throws IOException {
        long startTime = System.currentTimeMillis();
        String[] queryTerms = string.split(" ");
        queryTerms = removeStopWords(queryTerms);
        PriorityQueueDisjunctiveDAAT disjunctiveDAAT = new PriorityQueueDisjunctiveDAAT(queryTerms);
        ArrayList<Integer> results = disjunctiveDAAT.processQuery();
        System.out.println(results);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("NEW-DISJUNCTIVE finished in " + (float)elapsedTime/1000 +"sec");
    }

    public static void testNoPriorityQueueDisjunctive(String string) throws IOException {
        long startTime = System.currentTimeMillis();
        String[] queryTerms = string.split(" ");
        queryTerms = removeStopWords(queryTerms);
        DisjunctiveDAAT noPriorityQueueDisjunctive = new DisjunctiveDAAT(queryTerms);
        ArrayList<Integer> results = noPriorityQueueDisjunctive.processQuery();
        System.out.println(results);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("NO-PRIORITY-QUEUE-DISJUNCTIVE finished in " + (float)elapsedTime/1000 +"sec");
    }

    public static void testCompressedReading() throws IOException {
        Index index = new Index("");
        int numberOfBlocks = index.getNumberOfBlocks();
        BlockMerger blockMerger = new BlockMerger(numberOfBlocks);
        blockMerger.mergeBlocks();
    }

    public static void checkLexiconEntry(String string) throws IOException {
        LexiconHandler lexiconHandler = new LexiconHandler();
        InvertedIndexHandler invertedIndexHandler = new InvertedIndexHandler();
        ByteBuffer entryBuffer = lexiconHandler.findTermEntry(string);
        String term = lexiconHandler.getTerm(entryBuffer);
        int documentFrequency = lexiconHandler.getDf(entryBuffer);
        int offset = lexiconHandler.getOffset(entryBuffer);
        float termUpperBoundScore = lexiconHandler.getTermUpperBoundScore(entryBuffer);
        PostingListBlock postingListBlock = invertedIndexHandler.getPostingList(offset,documentFrequency);
        System.out.println("term = " + term);
        System.out.println("postingList = " + postingListBlock);
        System.out.println("offset = " + offset);
        System.out.println("documentFrequency = " + documentFrequency);
        System.out.println("termUpperBoundScore = " + termUpperBoundScore);
    }

    public void testIntegerAndFloat(Integer testInteger,Float testFloat) {
        testInteger = 1;
        testFloat = Float.valueOf(2);
    }
}



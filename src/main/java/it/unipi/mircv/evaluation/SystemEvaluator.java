package it.unipi.mircv.evaluation;
import it.unipi.mircv.Config.Score;
import it.unipi.mircv.Config.QueryProcessor;
import it.unipi.mircv.File.DocumentIndexFileHandler;
import it.unipi.mircv.Query.*;
import it.unipi.mircv.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static it.unipi.mircv.Config.MAX_NUM_DOC_RETRIEVED;
import static it.unipi.mircv.Utils.*;

public class SystemEvaluator {

    public static void evaluateSystemTime(String tsvFile, QueryProcessor queryProcessor, Score score, boolean stopWordRemoval, boolean stemming) throws IOException {

        ArrayList<Query> queries = new ArrayList<>();
        loadQueriesFromFile(tsvFile, queries);

        ArrayList<Long> resultsTimes = new ArrayList<>(queries.size());
        for (Query query : queries) {
            resultsTimes.add(testQueryTime(query.getQueryText(), queryProcessor, score, stopWordRemoval, stemming));
        }

        System.out.println("results times: " + resultsTimes);
        System.out.println("mean: " + computeMean(resultsTimes));
    }

    public static void createFileQueryResults(String fileName, String queryFile, QueryProcessor queryProcessor, Score scoreType, boolean stopWordRemoval, boolean stemming) throws IOException {
        StringBuilder stringToWrite;
        String fixed = "Q0";
        String runId = "0";
        float docScore = 0f;
        ArrayList<Query> queries = new ArrayList<>();
        loadQueriesFromFile(queryFile, queries);
        deleteFile(fileName);

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        for (Query query : queries) {
            String[] queryResults = queryResult(query.getQueryText(), queryProcessor, scoreType, stopWordRemoval, stemming);
            for(int i = 0; i < queryResults.length; i++){
                //this line must be here, otherwise the buffer size could not be enough
                stringToWrite = new StringBuilder();
                stringToWrite.append(query.getQueryId()).append(" ").append(fixed).append(" ");   //queryId fixed
                stringToWrite.append(queryResults[i]).append(" ");                                 //docNo rank
                stringToWrite.append(i + 1).append(" ");                                           //rank
                stringToWrite.append(docScore).append(" ").append(runId).append("\n");             //score runId
                writer.write(stringToWrite.toString());
            }
            writer.flush();
        }
    }

    public static void loadQueriesFromFile(String tsvFile, ArrayList<Query> queries)  throws IOException{
        String line;
        BufferedReader br = new BufferedReader(new FileReader(tsvFile));
        while ((line = br.readLine()) != null) {
            String[] data = line.split("\t");
            int queryId = Integer.parseInt(data[0]);
            String queryText = data[1];
            queries.add(new Query(queryId, queryText));
        }
    }

    public static String[] queryResult(String query, QueryProcessor queryProcessor, Score score, boolean stopWordRemoval, boolean stemming) throws IOException {
        DocumentIndexFileHandler documentIndexHandler = new DocumentIndexFileHandler();
        String[] queryTerms = Utils.tokenization(query);

        if (stopWordRemoval) queryTerms = removeStopWords(queryTerms);
        if (stemming) stemPhrase(queryTerms);

        System.out.println("final query: " + Arrays.toString(queryTerms)); //DEBUG

        ArrayList<Integer> results = new ArrayList<>(MAX_NUM_DOC_RETRIEVED);

        switch (queryProcessor) {
            case DISJUNCTIVE -> {
                results = new DisjunctiveDAAT(queryTerms).processQuery();
            }
            case CONJUNCTIVE -> {
                results = new ConjunctiveDAAT(queryTerms).processQuery();
            }
            case DISJUNCTIVE_MAX_SCORE -> {
                results = new MaxScoreDisjunctive(queryTerms).computeMaxScore();
            }
            case CONJUNCTIVE_MAX_SCORE -> {
                results = new MaxScore(queryTerms).computeMaxScore();
            }
        }
        return documentIndexHandler.getDocNoREVERSE(results);
    }

    public static long testQueryTime(String query, QueryProcessor queryProcessor, Score score, boolean stopWordRemoval, boolean stemming) throws IOException {
        DocumentIndexFileHandler documentIndexHandler = new DocumentIndexFileHandler();
        long startTime = System.currentTimeMillis();
        String[] queryTerms = Utils.tokenization(query);

        if (stopWordRemoval) queryTerms = removeStopWords(queryTerms);
        if (stemming) stemPhrase(queryTerms);

        System.out.println("final query: " + Arrays.toString(queryTerms)); //DEBUG

        ArrayList<Integer> results = new ArrayList<>(MAX_NUM_DOC_RETRIEVED);

        switch (queryProcessor) {
            case DISJUNCTIVE -> {
                results = new DisjunctiveDAAT(queryTerms).processQuery();
            }
            case CONJUNCTIVE -> {
                results = new ConjunctiveDAAT(queryTerms).processQuery();
            }
            case DISJUNCTIVE_MAX_SCORE -> {
                results = new MaxScoreDisjunctive(queryTerms).computeMaxScore();
            }
            case CONJUNCTIVE_MAX_SCORE -> {
                results = new MaxScore(queryTerms).computeMaxScore();
            }
        }

        System.out.println(Arrays.toString(documentIndexHandler.getDocNoREVERSE(results)));
        return System.currentTimeMillis() - startTime;
    }

    public static double computeMean(ArrayList<Long> list) {
        long sum = 0L;
        for (long num : list) {
            sum += num;
        }
        return (double) sum / list.size();
    }
}
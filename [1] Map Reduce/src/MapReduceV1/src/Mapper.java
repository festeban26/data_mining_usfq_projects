package com.festeban26;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Mapper extends Thread {

    private static ArrayList<HashMap<String, Integer>> sMappingBlocks = new ArrayList<>();
    private static ArrayList<String> sFicheros;
    private static int NUM_MAPPER_MAX_NODES = 12;

    private String mTextToMap;
    private int mIndex;

    private Mapper(String textToMap, int mappingBlockIndex) {
        mTextToMap = textToMap;
        mIndex = mappingBlockIndex;
    }

    public void run() {
        StringTokenizer stringTokenizer = new StringTokenizer(mTextToMap.toUpperCase());
        HashMap<String, Integer> ficheroHashMap = new HashMap<>();
        while (stringTokenizer.hasMoreElements()) {
            String currentString = stringTokenizer.nextElement().toString();
            if (!ficheroHashMap.containsKey(currentString))
                ficheroHashMap.put(currentString, 1);
            else {
                int currentCount = ficheroHashMap.get(currentString);
                ficheroHashMap.put(currentString, currentCount + 1);
            }
        }
        sMappingBlocks.set(mIndex, ficheroHashMap);
    }

    static void setFicheros(ArrayList<String> ficheros) {
        sFicheros = ficheros;
    }

    static ArrayList<HashMap<String, Integer>> execute() {

        for (int i = 0; i < sFicheros.size(); i++)
            sMappingBlocks.add(null);

        ExecutorService pool = Executors.newFixedThreadPool(NUM_MAPPER_MAX_NODES);
        for (int i = 0; i < sFicheros.size(); i++)
            pool.execute(new Mapper(sFicheros.get(i), i));
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            return sMappingBlocks;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}

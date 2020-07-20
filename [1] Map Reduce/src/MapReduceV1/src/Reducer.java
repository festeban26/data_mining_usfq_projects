package com.festeban26;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Reducer extends Thread {

    private static ArrayList<ArrayList<Tuple>> sIntermediateSplittingArrayList;
    private static HashMap<String, Integer> sReducerHashMap = new HashMap<>();
    private static int NUM_REDUCER_MAX_NODES = 3;

    private static final Object sLock = new Object();

    private int mIndex;

    private Reducer(int index) {
        mIndex = index;
    }

    public void run() {
        // REDUCER
        int repetitionCounter = 0;
        ArrayList<Tuple> currentWordBlock = sIntermediateSplittingArrayList.get(mIndex);
        String currentToken = currentWordBlock.get(0).getKey();
        for (int j = 0; j < currentWordBlock.size(); j++)
            repetitionCounter += currentWordBlock.get(j).getValue();
        // COMBINER
        addToReducerHashMap(currentToken, repetitionCounter);
    }

    private void addToReducerHashMap(String token, int repetitionCounter) {
        synchronized (sLock) {
            sReducerHashMap.put(token, repetitionCounter);
        }
    }

    static void setIntermediateSplittingArray(ArrayList<ArrayList<Tuple>> intermediateSplittingArrayList) {
        sIntermediateSplittingArrayList = intermediateSplittingArrayList;
    }

    static HashMap<String, Integer> execute() {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_REDUCER_MAX_NODES);
        for (int i = 0; i < sIntermediateSplittingArrayList.size(); i++)
            pool.execute(new Reducer(i));
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            return sReducerHashMap;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}

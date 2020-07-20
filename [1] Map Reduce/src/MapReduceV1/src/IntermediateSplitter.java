package com.festeban26;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IntermediateSplitter extends Thread {

    private static int NUM_INTERMEDIATE_SPLITTER_MAX_NODES = 12;

    private static ArrayList<ArrayList<Tuple>> sIntermediateSplittingArrayList = new ArrayList();
    private static HashMap<String, Integer> sIndexesOnIntermediateSplittingArrayList = new HashMap();
    private static ArrayList<HashMap<String, Integer>> sMappingBlocks;
    private int mIndex;

    private static final Object sAddingNewSplittingBlock_Lock = new Object();

    private IntermediateSplitter(int mappingBlockIndex) {
        mIndex = mappingBlockIndex;
    }

    public void run() {
        for (HashMap.Entry<String, Integer> entry : sMappingBlocks.get(mIndex).entrySet()) {
            synchronized (sAddingNewSplittingBlock_Lock) {
                if (!sIndexesOnIntermediateSplittingArrayList.containsKey(entry.getKey())) {
                    ArrayList<Tuple> newArrayList = new ArrayList<>();
                    newArrayList.add(new Tuple(entry.getKey(), entry.getValue()));
                    sIntermediateSplittingArrayList.add(newArrayList);
                    sIndexesOnIntermediateSplittingArrayList.put(entry.getKey(),
                            sIntermediateSplittingArrayList.indexOf(newArrayList));
                } else {
                    int index = sIndexesOnIntermediateSplittingArrayList.get(entry.getKey());
                    sIntermediateSplittingArrayList.get(index).add(new Tuple(entry.getKey(), entry.getValue()));
                }
            }
        }
    }

    static void setMappingBlocks(ArrayList<HashMap<String, Integer>> mappingBlocks) {
        sMappingBlocks = mappingBlocks;
    }

    static ArrayList<ArrayList<Tuple>> execute() {

        ExecutorService pool = Executors.newFixedThreadPool(NUM_INTERMEDIATE_SPLITTER_MAX_NODES);
        for (int i = 0; i < sMappingBlocks.size(); i++)
            pool.execute(new IntermediateSplitter(i));
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            return sIntermediateSplittingArrayList;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

}

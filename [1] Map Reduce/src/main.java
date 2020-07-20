package com.festeban26;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
Esta clase esta hecha para el trabajo con textos descargados del proyecto Gutenberg
https://www.gutenberg.org
 */
public class Main {

    private static int POOL_MAX_THREADS = 5;
    private static Charset DOCUMENT_CHARSET = StandardCharsets.UTF_8;
    private static int NUM_PARAGRAPHS_FOR_EACH_MAP_NODE = 3;

    public static void main(String[] args) {
        args = new String[1];
        args[0] = "C:/IdeaProjects/raw/text.txt";
        Path sourcePath = Paths.get(args[0]);

        // Splitting
        ArrayList<String> ficheros = null;
        if(Files.exists(sourcePath))
            ficheros = Splitter.split(sourcePath, NUM_PARAGRAPHS_FOR_EACH_MAP_NODE, DOCUMENT_CHARSET);
        else{
            System.out.println("ERROR! The file: " + args[0] + " does not exists.");
            System.exit(-1);
        }

        // Mapping
        ArrayList<HashMap<String, Integer>> mappingBlocks = new ArrayList<>();
        for(int i = 0; i < ficheros.size();  i++)
            mappingBlocks.add(null);

        ExecutorService pool = Executors.newFixedThreadPool(POOL_MAX_THREADS);

        Runnable map = new Runnable() {
            @Override
            public void run() {
                
            }
        };

        for(int i = 0; i < ficheros.size();  i++){
            StringTokenizer stringTokenizer = new StringTokenizer(ficheros.get(i).toUpperCase());
            HashMap<String, Integer> ficheroHashMap = new HashMap<>();
            while (stringTokenizer.hasMoreElements()) {
                String currentString = stringTokenizer.nextElement().toString();
                if(!ficheroHashMap.containsKey(currentString))
                    ficheroHashMap.put(currentString, 1);
                else{
                    int currentCount = ficheroHashMap.get(currentString);
                    ficheroHashMap.put(currentString, currentCount + 1);
                }
            }
            mappingBlocks.set(i, ficheroHashMap);
        }

        // Intermediate Splitting
        ArrayList<ArrayList<Tuple>> intermediateSplittingArrayList = new ArrayList();
        HashMap<String, Integer> indexesOnIntermediateSplittingArrayList = new HashMap();

        for(int i = 0; i < mappingBlocks.size();  i++){
            for (HashMap.Entry<String, Integer> entry : mappingBlocks.get(i).entrySet()) {
                if(!indexesOnIntermediateSplittingArrayList.containsKey(entry.getKey())){
                    ArrayList<Tuple> newArrayList = new ArrayList<>();
                    newArrayList.add(new Tuple(entry.getKey(), entry.getValue()));
                    intermediateSplittingArrayList.add(newArrayList);
                    indexesOnIntermediateSplittingArrayList.put(entry.getKey(),
                            intermediateSplittingArrayList.indexOf(newArrayList));
                }
                else{
                    int index = indexesOnIntermediateSplittingArrayList.get(entry.getKey());
                    ArrayList<Tuple> existingArrayList = intermediateSplittingArrayList.get(index);
                    existingArrayList.add(new Tuple(entry.getKey(), entry.getValue()));
                }
            }
        }
        indexesOnIntermediateSplittingArrayList.size();

        // REDUCING
        HashMap<String, Integer> reducerHashMap = new HashMap<>();
        for(int i = 0; i < intermediateSplittingArrayList.size();  i++){
            int repetitionCounter = 0;
            ArrayList<Tuple> currentWordBlock = intermediateSplittingArrayList.get(i);
            String currentToken = currentWordBlock.get(0).getKey();
            for(int j = 0; j < currentWordBlock.size(); j ++)
                repetitionCounter += currentWordBlock.get(j).getValue();
            reducerHashMap.put(currentToken, repetitionCounter);
        }


        // Display Result
        Map<String, Integer> mapSortedByKey = new TreeMap<>(reducerHashMap);

        Map<String,Integer> sortedByValueDesc =
                mapSortedByKey.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        //.limit(10)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        System.out.println("");
    }
}

package com.festeban26;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/*
Esta clase está hecha para el trabajo con textos descargados del proyecto Gutenberg
https://www.gutenberg.org
 */
public class Main {
    private static Charset DOCUMENT_CHARSET = StandardCharsets.UTF_8;
    private static int NUM_PARAGRAPHS_FOR_EACH_MAP_NODE = 3;

    public static void main(String[] args) {
        Path sourcePath = Paths.get(args[0]);

        // SPLITTING
        ArrayList<String> ficheros = null;
        if(Files.exists(sourcePath))
            ficheros = Splitter.split(sourcePath, NUM_PARAGRAPHS_FOR_EACH_MAP_NODE, DOCUMENT_CHARSET);
        else{
            System.out.println("ERROR! The file: " + args[0] + " does not exists.");
            System.exit(-1);
        }

        // MAPPING
        Mapper.setFicheros(ficheros);
        ArrayList<HashMap<String, Integer>> mappingBlocks =  Mapper.execute();

        // INTERMEDIATE SPITTING
        IntermediateSplitter.setMappingBlocks(mappingBlocks);
        ArrayList<ArrayList<Tuple>> intermediateSplittingArrayList = IntermediateSplitter.execute();

        // REDUCING AND COMBINING
        Reducer.setIntermediateSplittingArray(intermediateSplittingArrayList);
        HashMap<String, Integer> reducerHashMap = Reducer.execute();

        // DISPLAY RESULTS
        Map<String, Integer> mapSortedByKey = new TreeMap<>(reducerHashMap);

        Map<String,Integer> mapSortedByValueDesc =
                mapSortedByKey.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        //.limit(10)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        System.out.println();
    }
}

package com.festeban26;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CsvReader {

    private static String CVS_SEPARATOR = ",";

    public static ArrayList<Point> getPoints(String filename) {
        String cwd = System.getProperty("user.dir");
        String filePath = cwd + File.separator + filename;
        System.out.println("==> Reading file (" + filePath + ")");
        File csvFile = new File(filePath);
        System.out.println("==> File (" + filePath + ") loaded.");

        String line;
        ArrayList<Point> points = new ArrayList<Point>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] rawCoordinates = line.split(CVS_SEPARATOR);
                ArrayList<Float> coordinates = Arrays.stream(rawCoordinates)
                        .map(Float::valueOf)
                        .collect(Collectors.toCollection(ArrayList::new));
                points.add(new Point(coordinates));
                System.out.println(coordinates);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }
}

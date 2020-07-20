package com.festeban26;


import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.Normalize;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main{

    private static Double MOMENTUM = 0.2;

    public static void main(String[] args)  throws Exception {

        String cwd = System.getProperty("user.dir");
        String filename = "subset.csv";
        String filePath = cwd + File.separator + filename;
        System.out.println("==> Reading file (" + filePath + ")");
        File file = new File(filePath);
        System.out.println("==> File (" + filePath + ") loaded.");

        System.out.println("==> Parsing CSV...");
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setFile(file);
        Instances data = new Instances(csvLoader.getDataSet());
        int classIndex = data.numAttributes() - 1;
        data.setClassIndex(classIndex);
        System.out.println("==> Data:");
        System.out.println(data.toSummaryString());
        System.out.println(data);

        Normalize filter = new Normalize();
        filter.setInputFormat(data);
        System.out.println("==> Normalized Data:");
        //System.out.println(Filter.useFilter(data, filter));

        DecimalFormat decimalFormatter = new DecimalFormat("#.00");
        String hiddenLayers = "a, 5";
        int iterMin = 10 / 10;
        int iterMax = 400 / 10 + 1;
        IntStream.range(3, 4).asDoubleStream().map(i -> i * 0.01).forEach(learningRate -> {

            ExecutorService executor = Executors.newFixedThreadPool(10);
            // Range from 10 to 1000
            IntStream.range(iterMin, iterMax).map(i -> i * 10).forEach(iterations -> {
                Runnable worker = new MultilayerPerceptronThread(data, learningRate, MOMENTUM, iterations,
                        hiddenLayers, 3);
                executor.execute(worker);
            });
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            TreeMap<Integer, Double> aucRecords = MultilayerPerceptronThread.getAccuracyRecord();
            ArrayList<Integer> xValues = new ArrayList<>();
            ArrayList<Double> yValues = new ArrayList<>();
            for (Map.Entry<Integer, Double> mapData : aucRecords.entrySet()) {
                xValues.add(mapData.getKey());
                yValues.add(mapData.getValue());
            }
            double[] xData = xValues.stream().mapToDouble(Integer::intValue).toArray();
            double[] yData = yValues.stream().mapToDouble(Double::doubleValue).toArray();

            String chartFilename = "./MLP_"
                    + hiddenLayers.split(",").length + " num.OfHiddenLayers_"
                    + hiddenLayers + "hiddenLayers"
                    + decimalFormatter.format(learningRate) + "learningRate_"
                    + decimalFormatter.format(MOMENTUM) + "momentum";
            String seriesName = "Num of hidden layers: " + hiddenLayers.split(",").length + "\n"
                    + "hiddenLayers: " + hiddenLayers + "\n"
                    + "LR: " + decimalFormatter.format(learningRate) + "\n"
                    + "m: " + decimalFormatter.format(MOMENTUM);
            // Create Chart
            XYChart chart = QuickChart.getChart("ANN(Multilayer Perceptron)", "iterations",
                    "Accuracy", seriesName, xData, yData);

            try {
                BitmapEncoder.saveBitmap(chart, chartFilename, BitmapEncoder.BitmapFormat.PNG); // Save it
            } catch (IOException e) {
                e.printStackTrace();
            }
            MultilayerPerceptronThread.resetAccuracyRecords();

        });

        double[] xData = new double[2];
        double[] yData = new double[2];
        XYChart chart = QuickChart.getChart("AUC - ROC curve", "FPR", "TPR",
                "NONE", xData, yData);

        // Range from 10 to 1000
        IntStream.range(iterMin, 11).map(i -> i * 40).forEach(iterations -> {
            Map<Integer, TreeMap<Double, Double>> aucRocRecords = MultilayerPerceptronThread.getAucRocRecords();
            TreeMap<Double, Double> aucRocRecord = aucRocRecords.get(iterations); // num of iterations as index
            ArrayList<Double> xValues = new ArrayList<>();
            ArrayList<Double> yValues = new ArrayList<>();
            for (Map.Entry<Double, Double> mapData : aucRocRecord.entrySet()) {
                xValues.add(mapData.getKey());
                yValues.add(mapData.getValue());
            }
            double[] x = xValues.stream().mapToDouble(Double::doubleValue).toArray();
            double[] y = yValues.stream().mapToDouble(Double::doubleValue).toArray();
            String seriesName = iterations + " it";
            XYSeries series = chart.addSeries(seriesName, x, y);
            series.setMarker(SeriesMarkers.NONE);
        });

        try {
            BitmapEncoder.saveBitmap(chart, "test", BitmapEncoder.BitmapFormat.PNG); // Save it
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished all threads");
    }
}

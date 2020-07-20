package com.festeban26;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Debug;
import weka.core.Instances;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MultilayerPerceptronThread implements Runnable {

    private static long sRandomSeed = 0;//System.currentTimeMillis();
    private static Map<Integer, Double> sAccuracyRecords = new ConcurrentHashMap<>();
    private static Map<Integer, TreeMap<Double, Double>> sAucRocRecords = new ConcurrentHashMap<>();
    private static DecimalFormat sDecimalFormatter_TwoDecimals = new DecimalFormat("#.00");
    private static DecimalFormat sDecimalFormatter_FourDecimals = new DecimalFormat("#.0000");

    private Instances mData;
    private double mLearningRate;
    private double mMomentum;
    private int mIterations;
    private String mHiddenLayers;
    private int mNumOfFolds;

    public MultilayerPerceptronThread(Instances data, double learningRate, double momentum, int iterations,
                                      String hiddenLayers, int numOfFolds) {
        mData = data;
        mLearningRate = learningRate;
        mMomentum = momentum;
        mIterations = iterations;
        mHiddenLayers = hiddenLayers;
        mNumOfFolds = numOfFolds;
    }

    @Override
    public void run() {
        //Instance of ANN
        MultilayerPerceptron multilayerPerceptron = new MultilayerPerceptron();

        //Setting Parameters
        // Learning rate for the backpropagation algorithm. (Value should be between 0 - 1, Default = 0.3)
        multilayerPerceptron.setLearningRate(mLearningRate);
        // Momentum rate for the backpropagation algorithm. (Value should be between 0 - 1, Default = 0.2).
        multilayerPerceptron.setMomentum(mMomentum);
        multilayerPerceptron.setTrainingTime(mIterations);
        multilayerPerceptron.setHiddenLayers(mHiddenLayers);
        int numOfHiddenLayers = mHiddenLayers.split(",").length;
        try {
            multilayerPerceptron.buildClassifier(mData);
            Evaluation eval = new Evaluation(mData);
            eval.crossValidateModel(multilayerPerceptron, mData, mNumOfFolds, new Debug.Random(sRandomSeed));

            //System.out.println(eval.toSummaryString()); //Summary of Training
            double accuracy = eval.correct() / eval.numInstances();
            sAccuracyRecords.put(mIterations, accuracy);
            ThresholdCurve tc = new ThresholdCurve();
            Instances result = tc.getCurve(eval.predictions());

            TreeMap<Double, Double> currentAucRocRecords = new TreeMap<>();
            double[] FPR = result.attributeToDoubleArray(4);
            double[] TPR = result.attributeToDoubleArray(5);
            for (int i = 0; i < FPR.length; i++)
                currentAucRocRecords.put(FPR[i], TPR[i]);
            sAucRocRecords.put(mIterations, currentAucRocRecords);

            System.out.println(Thread.currentThread().getName()
                    + "numHiddenLayers:(" + numOfHiddenLayers + "), "
                    + "hiddenLayers: (" + mHiddenLayers + "), "
                    + "lr:(" + MultilayerPerceptronThread.formatDouble(mLearningRate, 2) + "), "
                    + "m:(" + mMomentum + "), "
                    + "it:(" + mIterations + "), "
                    + "accuracy:(" + MultilayerPerceptronThread.formatDouble(accuracy, 4) + ")"
                    + eval.toSummaryString()
            + Arrays.deepToString(eval.confusionMatrix()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String formatDouble(Double doubleNum, int numOfDecimals) {
        if (numOfDecimals == 2)
            return sDecimalFormatter_TwoDecimals.format(doubleNum);
        else
            return sDecimalFormatter_FourDecimals.format(doubleNum);
    }

    public static TreeMap<Integer, Double> getAccuracyRecord() {
        return new TreeMap<>(sAccuracyRecords);
    }

    public static void resetAccuracyRecords() {
        sAccuracyRecords.clear();
    }

    public static Map<Integer, TreeMap<Double, Double>> getAucRocRecords() {
        return sAucRocRecords;
    }
}

package com.festeban26;

import java.util.ArrayList;

public class Main {

    private static final double EPSILON = 1E-4;
    private static final double BETA = 0.8;
    private static double sSPIDER_TRAP;
    private static double sDEAD_END = 0.9;
    private static boolean SPIDER_TRAP_DETECTED = false;

    private static boolean sHandleDeadEnd = true;
    private static boolean sHandleSpiderTrap = true;

    private static double[][] noDeadEnd_NoSpiderTrap = new double[][]{
            {0.5, 0.5, 0},
            {0.5, 0, 1},
            {0, 0.5, 0},
    };

    private static double[][] noDeadEnd_butSpiderTrap = new double[][]{
            {0.5, 0.5, 0},
            {0.5, 0, 0},
            {0, 0.5, 1},
    };

    private static double[][] deadEnd_butNoSpiderTrap = new double[][]{
            {0.5, 0.5, 0},
            {0.5, 0, 0},
            {0, 0.5, 0},
    };

    /* Hoja Esteban
    = new double [][] {
                {0, 0, 0, 0, 0},
                {1, 0.5, 1, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0.5, 0, 0, 0},
                {0, 0, 0, 1, 0}
        };
     */

    /* Esteban
    = new double[][]{
                // A    B     C     D     E     F     G     H     I     J     K     L     M     N     O     P     Q     R     S     T
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00}
        };
     */

    /* DIEGO
    = new double[][]{
                {0.00,0.00,0.17,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.33,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.00,0.00,0.17,0.25,0.00,0.17,0.20,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.33},
                {0.00,0.17,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.00,0.17,0.17,0.00,0.00,0.17,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.12,0.17,0.17,0.25,0.50,0.00,0.00,1.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.12,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.33,0.00,0.00,0.00,0.00,0.33},
                {0.00,0.00,0.00,0.25,0.00,0.17,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.00,0.17,0.00,0.25,0.50,0.00,0.20,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.12,0.00,0.00,0.00,0.00,0.17,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.33,0.00,0.00,0.00,0.00,0.33},
                {0.12,0.17,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.33,0.25,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.00,0.00,0.17,0.00,0.00,0.00,0.00,0.00,0.00,0.00,1.00,0.00,0.00,0.00,0.00,0.00,0.25,0.00,0.00,0.00},
                {0.00,0.00,0.17,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.25,0.00,0.00,0.00},
                {0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.25,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.12,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.12,0.00,0.00,0.00,0.00,0.00,0.20,0.00,0.50,0.00,0.00,0.00,0.00,0.25,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.00,0.00,0.00,0.00,0.00,0.17,0.20,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.33,1.00,0.00,0.00,0.00,0.00},
                {0.12,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.33,0.33,0.00,0.00,0.00,0.00,0.00,0.00,0.00},
                {0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.50,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.25,0.00,0.00,0.00},
                {0.12,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.33,0.33,0.00,0.00,0.00,0.25,0.00,0.00,0.00},
                {0.00,0.17,0.00,0.00,0.00,0.17,0.20,0.00,0.00,0.00,0.00,0.00,0.00,0.25,0.00,0.00,0.00,0.00,0.00,0.00}
        };
     */

    public static void main(String[] args) {

        System.out.println("Welcome to Page Rank Solver by festeban26");

        double[][] squareMatrix = new double[][]{
                {0.00, 0.00, 0.17, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.33, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.17, 0.25, 0.00, 0.17, 0.20, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.33},
                {0.00, 0.17, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.17, 0.17, 0.00, 0.00, 0.17, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.12, 0.17, 0.17, 0.25, 0.50, 0.00, 0.00, 1.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.12, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.33, 0.00, 0.00, 0.00, 0.00, 0.33},
                {0.00, 0.00, 0.00, 0.25, 0.00, 0.17, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.17, 0.00, 0.25, 0.50, 0.00, 0.20, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.12, 0.00, 0.00, 0.00, 0.00, 0.17, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.33, 0.00, 0.00, 0.00, 0.00, 0.33},
                {0.12, 0.17, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.33, 0.25, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.17, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 1.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.25, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.17, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.25, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.25, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.12, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.12, 0.00, 0.00, 0.00, 0.00, 0.00, 0.20, 0.00, 0.50, 0.00, 0.00, 0.00, 0.00, 0.25, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.17, 0.20, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.33, 1.00, 0.00, 0.00, 0.00, 0.00},
                {0.12, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.33, 0.33, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.50, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.25, 0.00, 0.00, 0.00},
                {0.12, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.33, 0.33, 0.00, 0.00, 0.00, 0.25, 0.00, 0.00, 0.00},
                {0.00, 0.17, 0.00, 0.00, 0.00, 0.17, 0.20, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.25, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00}
        };

        double[] initialVector = getInitialVector_1ByN(squareMatrix.length);
        startPowerIterator(squareMatrix, initialVector);
        System.out.println("\n===================== PROCESS FINISHED =====================");
    }

    private static void startPowerIterator(double[][] squareMatrix, double[] initialVector) {
        sSPIDER_TRAP = 0.1 * (1 / (double) squareMatrix.length);

        System.out.println("Power Iterator " + "(Random Walker) started with:\n" +
                "- EPSILON of " + EPSILON + "\n" +
                "- SPIDER TRAP detector coeff set to " + sSPIDER_TRAP);
        double[][] originalMatrix = new double[squareMatrix.length][squareMatrix.length];

        for (int row = 0; row < squareMatrix.length; row++)
            System.arraycopy(squareMatrix[row], 0, originalMatrix[row], 0, squareMatrix.length);

        int returnValue;
        while ((returnValue = powerIterator(squareMatrix, initialVector)) != 0) {
            switch (returnValue) {
                case 1:
                    // Spider trap
                    SPIDER_TRAP_DETECTED = true;
                    if (sHandleSpiderTrap)
                        squareMatrix = handleSpiderTrap(squareMatrix);
                    break;
                case -1:
                    if (sHandleDeadEnd)
                        squareMatrix = handleDeadEnd(squareMatrix);
                    break;
            }
        }
    }

    private static int powerIterator(double[][] squareMatrix, double[] initialVector) {
        // 0: sin inconvenientes
        // 1: spider trap
        // -1: dead end

        System.out.println("Matrix: ");
        printSquareMatrix(squareMatrix);

        ArrayList<ArrayList<Double>> powerIteratorMatrix = new ArrayList<>();

        double[] r_t = initialVector;
        double[] r_tp1 = multiplyVectorByMatrix(r_t, squareMatrix);

        powerIteratorMatrix.add(getArrayListFromArray(r_t));
        powerIteratorMatrix.add(getArrayListFromArray(r_tp1));

        ArrayList<Double> columnSum = new ArrayList<>();

        boolean firstRun = true;

        while (Double.compare(normL1(r_tp1, r_t), EPSILON) > 0) {

            r_t = r_tp1;
            r_tp1 = multiplyVectorByMatrix(r_t, squareMatrix);
            powerIteratorMatrix.add(getArrayListFromArray(r_tp1));

            columnSum.clear();
            for (int i = 0; i < powerIteratorMatrix.size(); i++)
                columnSum.add(0.0);

            System.out.println("Power Iterator matrix:");
            for (int column = 0; column < powerIteratorMatrix.get(0).size(); column++) {
                // Print header
                if (column == 0) {
                    for (int row = 0; row < powerIteratorMatrix.size(); row++) {
                        String header = "r_" + row;
                        System.out.print(String.format("%-6s", header));
                    }
                    System.out.println();
                }

                for (int row = 0; row < powerIteratorMatrix.size(); row++) {
                    System.out.printf("%.3f ", powerIteratorMatrix.get(row).get(column));

                    double valueToBeAdded = powerIteratorMatrix.get(row).get(column);
                    double value = columnSum.get(row) + valueToBeAdded;
                    columnSum.set(row, value);
                    if (!SPIDER_TRAP_DETECTED && !firstRun)
                        if (Double.compare(powerIteratorMatrix.get(row).get(column), sSPIDER_TRAP) < 0) {
                            System.out.println("\n============= SPIDER TRAP DETECTED =============");
                            return 1;
                        }
                }
                System.out.println();
            }

            System.out.println("SUM ----------------------------");
            for (int i = 0; i < columnSum.size(); i++) {
                System.out.printf("%.3f ", columnSum.get(i));
                if (Double.compare(columnSum.get(i), sDEAD_END) < 0) {
                    System.out.println("\n============= DEAD END DETECTED =============");
                    firstRun = false;
                    return -1;
                }
            }
            System.out.println("\n");
            firstRun = false;
        }
        return 0;
    }

    private static double[][] handleSpiderTrap(double[][] originalSquareMatrix) {
        double aux = (1 / (double) originalSquareMatrix.length) * (1 - BETA);

        double[][] newMatrix = new double[originalSquareMatrix.length][originalSquareMatrix.length];

        for (int row = 0; row < originalSquareMatrix.length; row++)
            System.arraycopy(originalSquareMatrix[row], 0, newMatrix[row], 0, originalSquareMatrix.length);

        for (int row = 0; row < originalSquareMatrix.length; row++)
            for (int column = 0; column < originalSquareMatrix.length; column++)
                newMatrix[row][column] = originalSquareMatrix[row][column] * BETA + aux;
        return newMatrix;
    }

    private static double[][] handleDeadEnd(double[][] originalSquareMatrix) {

        ArrayList<Integer> columnsWithDeadEnds = new ArrayList<>();

        System.out.println("Checking for dead ends...");
        for (int column = 0; column < originalSquareMatrix.length; column++) {
            int counter = 0;
            for (int row = 0; row < originalSquareMatrix.length; row++)
                if (originalSquareMatrix[row][column] == 0)
                    counter++;
            if (counter == originalSquareMatrix.length)
                columnsWithDeadEnds.add(column);
        }

        if (!columnsWithDeadEnds.isEmpty()) {
            System.out.print("Dead end(s) found! Column(s) " + columnsWithDeadEnds.toString());
            System.out.println(". Dealing with dead ends, adjusting matrix...");

            int squareMatrixSize = originalSquareMatrix.length;

            for (int row = 0; row < originalSquareMatrix.length; row++)
                for (int column = 0; column < originalSquareMatrix.length; column++)
                    if (columnsWithDeadEnds.contains(column))
                        originalSquareMatrix[row][column] = 1 / (double) squareMatrixSize;

            System.out.println("Adjusted matrix: ");
            printSquareMatrix(originalSquareMatrix);
        } else
            System.out.println("========= NO DEAD END FOUND =========");

        return originalSquareMatrix;
    }

    private static ArrayList<Double> getArrayListFromArray(double[] arr) {

        ArrayList<Double> arrayList = new ArrayList<>();
        for (int i = 0; i < arr.length; i++)
            arrayList.add(arr[i]);
        return arrayList;
    }

    private static double normL1(double[] vector_tp1, double[] vector_t) {
        double sum = 0;
        for (int i = 0; i < vector_tp1.length; i++)
            sum += Math.pow(vector_tp1[i] - vector_t[i], 2);
        double normL1 = Math.sqrt(sum);
        return normL1;
    }

    private static double[] multiplyVectorByMatrix(double[] vector, double[][] matrix) {
        double[] result = new double[vector.length];
        for (int row = 0; row < vector.length; row++) {
            double sum = 0;
            for (int column = 0; column < vector.length; column++)
                sum += matrix[row][column] * vector[column];
            result[row] = sum;
        }
        return result;
    }

    private static double[] getInitialVector_1ByN(int matrixSize) {
        double initialValue = 1 / (double) matrixSize;
        return getInitialVector(initialValue, matrixSize);
    }

    private static double[] getInitialVector_1(int matrixSize) {
        double initialValue = 1;
        return getInitialVector(initialValue, matrixSize);
    }

    private static double[] getInitialVector(double initialValue, int matrixSize) {
        double[] vector = new double[matrixSize];
        for (int i = 0; i < matrixSize; i++)
            vector[i] = initialValue;
        return vector;
    }

    private static void printSquareMatrix(double[][] squareMatrix) {

        ArrayList<Double> columnSum = new ArrayList<>();

        for (int i = 0; i < squareMatrix.length; i++)
            columnSum.add(0.0);

        int squareMatrixSize = squareMatrix.length;
        for (int i = 0; i < squareMatrixSize; i++) {
            for (int j = 0; j < squareMatrixSize; j++) {
                System.out.printf("%.2f ", squareMatrix[i][j]);
                columnSum.set(j, columnSum.get(j) + squareMatrix[i][j]);
            }
            System.out.println();
        }

        System.out.println("SUM ----------------------------");
        for (int i = 0; i < columnSum.size(); i++)
            System.out.printf("%.2f ", columnSum.get(i));
        System.out.println("\n");


    }
}
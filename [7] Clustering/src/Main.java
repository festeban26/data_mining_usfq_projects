package com.festeban26;

import org.jzy3d.colors.Color;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.DoubleStream;

public class Main {

    private static String FILE_NAME = "test_2c_chainlink.csv";
    private static float REPRESENTATIVE_POINT_MOVEMENT_FRACTION = 0.2f;
    private static int MAX_VALUE_OF_K_TO_GRAPH = 5;
    private static double K_CLUSTERS_TO_VISUALIZE[] = {2, 3, 4};
    private static boolean VISUALIZE_HIERARCHICAL_CLUSTERING_RESULT_WITH_HIGHLIGHTED_REPRESENTATIVE_POINTS = true;
    private static boolean VISUALIZE_2ND_PHASE_BEGINNING_STATE = true;

    public static void main(String[] args) {

        ArrayList<Point> initialPoints = CsvReader.getPoints(FILE_NAME);
        TreeMap<Integer, Float> kD = new TreeMap<>();
        ArrayList<Cluster> hierarchicalClusters = new ArrayList<>();

        // Hierarchical clustering
        // Initially all points are a cluster
        for (Point point : initialPoints) {
            Cluster cluster = new Cluster();
            cluster.add(point);
            hierarchicalClusters.add(cluster);
        }
        while (hierarchicalClusters.size() > 1) {
            // Identify the two clusters that are closest together (distance between their centroids)
            float hierarchicalMinDistance = Float.MAX_VALUE;
            int indexOfFirstClusterToMerge = -1;
            int indexOfSecondClusterToMerge = -1;
            // Iterate clusters
            for (int firstClusterIndex = 0; firstClusterIndex < hierarchicalClusters.size() - 1; firstClusterIndex++) {
                Cluster firstCluster = hierarchicalClusters.get(firstClusterIndex);
                // Iterate the other cluster to compare
                for (int secondClusterIndex = firstClusterIndex + 1; secondClusterIndex < hierarchicalClusters.size(); secondClusterIndex++) {
                    Cluster secondCluster = hierarchicalClusters.get(secondClusterIndex);
                    Point firstCentroid = firstCluster.getCenter();
                    Point secondCentroid = secondCluster.getCenter();
                    float distanceBetweenCentroids = firstCentroid.calculateDistanceTo(secondCentroid);
                    for (Point p1 : firstCluster.getPoints())
                        for (Point p2 : secondCluster.getPoints()) {
                            float distanceBetweenPoints = p1.calculateDistanceTo(p2);
                            float weightedDistance = distanceBetweenPoints * 3 + distanceBetweenCentroids;
                            if (weightedDistance < hierarchicalMinDistance) {
                                hierarchicalMinDistance = weightedDistance;
                                indexOfFirstClusterToMerge = firstClusterIndex;
                                indexOfSecondClusterToMerge = secondClusterIndex;
                            }
                        }
                }
            }

            hierarchicalClusters.get(indexOfFirstClusterToMerge).add(hierarchicalClusters.get(indexOfSecondClusterToMerge).getPoints());
            hierarchicalClusters.remove(indexOfSecondClusterToMerge);

            if (hierarchicalClusters.size() <= MAX_VALUE_OF_K_TO_GRAPH) {

                boolean visualize = DoubleStream.of(K_CLUSTERS_TO_VISUALIZE).anyMatch(x -> x == hierarchicalClusters.size());

                ArrayList<Cluster> hierarchicalClustersClone = new ArrayList<>();
                for (Cluster hierarchicalCluster : hierarchicalClusters)
                    hierarchicalClustersClone.add(new Cluster(hierarchicalCluster)); // CLONE

                // SET REPRESENTATIVE POINTS FOR EACH HIERARCHICAL CLUSTER
                ArrayList<Cluster> pointsAndRepresentativesPointsClusters = new ArrayList<>(hierarchicalClustersClone);
                for (Cluster cluster : hierarchicalClustersClone) {
                    Cluster representativePointsCluster = new Cluster(cluster.getColorWithAlpha(1f));
                    representativePointsCluster.add(cluster.getRepresentativePoints());
                    pointsAndRepresentativesPointsClusters.add(representativePointsCluster);
                }
                if (visualize) {
                    if (VISUALIZE_HIERARCHICAL_CLUSTERING_RESULT_WITH_HIGHLIGHTED_REPRESENTATIVE_POINTS) {
                        Visualizer.visualize(pointsAndRepresentativesPointsClusters,
                                "(" + hierarchicalClustersClone.size() + ") clusters." +
                                        " HIERARCHICAL CLUSTERING RESULT WITH HIGHLIGHTED REPRESENTATIVE POINTS");
                    }
                }


                // Create the final clusters by setting their representative points
                ArrayList<Cluster> finalClusters = new ArrayList<>();
                for (Cluster cluster : hierarchicalClustersClone) {
                    Cluster finalCluster = new Cluster();
                    finalCluster.setRepresentativePoints(
                            cluster.getRepresentativePointsMovedTowardsCentroid(REPRESENTATIVE_POINT_MOVEMENT_FRACTION));
                    finalClusters.add(finalCluster);
                }

                ArrayList<Point> initialPointsClone =  new ArrayList<>();
                for(Point point : initialPoints)
                    initialPointsClone.add(new Point(point));

                // Visualize beginning of the Second Phase
                ArrayList<Cluster> secondPhaseCluster_justForVisualization = new ArrayList<>();
                secondPhaseCluster_justForVisualization.add(
                        new Cluster(initialPointsClone, Color.BLACK, 0.35f));
                for (Cluster finalCluster : finalClusters) {
                    Cluster representativePointsCluster = new Cluster(finalCluster.getColorWithAlpha(1f));
                    representativePointsCluster.add(finalCluster.getRepresentativePoints());
                    secondPhaseCluster_justForVisualization.add(representativePointsCluster);
                }
                if (visualize) {
                    if (VISUALIZE_2ND_PHASE_BEGINNING_STATE)
                        Visualizer.visualize(secondPhaseCluster_justForVisualization,
                                "(" + finalClusters.size() + ") clusters. BEGINNING OF 2ND PHASE.");
                }


                // CURE PASS 2 of 2
                for (Point point : initialPointsClone) {
                    float minDistance = Float.MAX_VALUE;
                    Cluster destinationCluster = null;
                    for (Cluster finalCluster : finalClusters) {
                        for (Point representativePoint : finalCluster.getRepresentativePoints()) {
                            float distance = point.calculateDistanceTo(representativePoint);
                            if (distance <= minDistance) {
                                minDistance = distance;
                                destinationCluster = finalCluster;
                            }
                        }
                    }
                    if (destinationCluster != null)
                        destinationCluster.add(point);
                }

                // RESULTS
                System.out.println("i) ==> Solution Clusters");
                // Visualize centroids
                ArrayList<Cluster> finalClustersPlusCentroids = new ArrayList<>(finalClusters);
                for (Cluster finalCluster : finalClusters) {
                    Cluster centroidCluster = new Cluster(Color.GREEN);
                    centroidCluster.add(finalCluster.getCenter());
                    finalClustersPlusCentroids.add(centroidCluster);
                    System.out.println(finalCluster);
                }

                if (visualize)
                    Visualizer.visualize(finalClustersPlusCentroids,
                            "(" + finalClusters.size() + ") clusters. FINAL CENTROIDS IN GREEN");


                float D = 0;
                for (Cluster cluster : finalClusters)
                    D += cluster.getAvgD();
                D = D / finalClusters.size();
                kD.put(finalClusters.size(), D);
            }
        } // END OF HIERARCHICAL CLUSTERING


        // KD GRAPH;
        ArrayList<Integer> xValues = new ArrayList<>();
        ArrayList<Float> yValues = new ArrayList<>();
        for (Map.Entry<Integer, Float> mapData : kD.entrySet()) {
            xValues.add(mapData.getKey());
            yValues.add(mapData.getValue());
        }
        double[] xData = xValues.stream().mapToDouble(Integer::intValue).toArray();
        double[] yData = yValues.stream().mapToDouble(d -> d).toArray();

        String chartFilename = "./AvgToCentroid";
        String seriesName = "K-D curve";
        // Create Chart
        XYChart chart = QuickChart.getChart("K vs D", "Number of clusters K",
                "AVG Distance to Centroid", seriesName, xData, yData);
        // Total within-clusters sum of squares

        try {
            BitmapEncoder.saveBitmap(chart, chartFilename, BitmapEncoder.BitmapFormat.PNG); // Save it
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
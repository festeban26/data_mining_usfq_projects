package com.festeban26;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.util.ArrayList;

public class Visualizer {

    public static void visualize(ArrayList<Cluster> clusters, String chartTitle){

        // Works only in 1D, 2D or 3D
        int dimensionality = clusters.get(0).getPoints().get(0).getCoordinates().size();
        if (dimensionality == 1 || dimensionality == 2 || dimensionality == 3)
        {
            int size = 0;
            for (Cluster cluster : clusters)
                size += cluster.getNumOfPoints();
            float x, y, z;
            Coord3d[] points_coord3d = new Coord3d[size];
            Color[] colorsFor_points_coord3d = new Color[size];

            int pointIndex = 0;
            for (int i = 0; i < clusters.size(); i++) {
                Cluster cluster = clusters.get(i);
                ArrayList<Point> points = cluster.getPoints();
                for (Point point : points) {
                    x = 0;
                    y = 0;
                    z = 0;
                    switch (point.getCoordinates().size()) {
                        case 1:
                            x = point.getCoordinates().get(0);
                            break;
                        case 2:
                            x = point.getCoordinates().get(0);
                            y = point.getCoordinates().get(1);
                            break;
                        case 3:
                            x = point.getCoordinates().get(0);
                            y = point.getCoordinates().get(1);
                            z = point.getCoordinates().get(2);
                            break;
                    }
                    points_coord3d[pointIndex] = new Coord3d(x, y, z);
                    colorsFor_points_coord3d[pointIndex] = cluster.getColor();
                    pointIndex++;
                }
            }

            // Create a drawable scatter with a colormap
            Scatter scatter = new Scatter(points_coord3d, colorsFor_points_coord3d);
            scatter.setWidth(10);
            // Create a chart and add scatter
            Chart chart = new Chart(Quality.Nicest);

            chart.getScene().add(scatter);
            ChartLauncher.openChart(chart, chartTitle);

        /*
        File outputfile = new File(clusters.size() + "clusters.png");
        try {
            ImageIO.write(chart.screenshot(), "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        }
    }
}

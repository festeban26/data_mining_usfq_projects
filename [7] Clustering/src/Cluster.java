package com.festeban26;

import org.jzy3d.colors.Color;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class Cluster {

    private Point mCenter;
    private ArrayList<Point> mPoints;
    private ArrayList<Point> mRepresentativePoints;
    private Color mColor;
    private Point mMaxCoordPoint;

    private static float alpha = 0.35f;

    private static ArrayList<Color> sSetOfColors = new ArrayList<>() {{
        add(new Color(0, 0, 0).alphaSelf(alpha));
        add(new Color(244, 210, 27).alphaSelf(alpha));
        add(new Color(244, 131, 51).alphaSelf(alpha));
        add(new Color(237, 94, 50).alphaSelf(alpha));
        add(new Color(227, 82, 147).alphaSelf(alpha));
        add(new Color(116, 116, 176).alphaSelf(alpha));
        add(new Color(0, 158, 204).alphaSelf(alpha));
        add(new Color(123, 197, 231).alphaSelf(alpha));
        add(new Color(83, 172, 176).alphaSelf(alpha));
        add(new Color(14, 159, 79).alphaSelf(alpha));
        add(new Color(223, 220, 47).alphaSelf(alpha));
        add(new Color(145, 195, 93).alphaSelf(alpha));
        add(new Color(150, 136, 109).alphaSelf(alpha));
        add(new Color(246, 172, 207).alphaSelf(alpha));
        add(new Color(181, 27, 125).alphaSelf(alpha));
        add(new Color(78, 46, 148).alphaSelf(alpha));
        add(new Color(174, 116, 70).alphaSelf(alpha));
        add(new Color(107, 45, 22).alphaSelf(alpha));
        add(new Color(251, 195, 158).alphaSelf(alpha));
        add(new Color(192, 228, 218).alphaSelf(alpha));
    }};

    private static Random sRandom = new Random(System.currentTimeMillis());

    Cluster() {
        mPoints = new ArrayList<>();
        mColor = sSetOfColors.get(sRandom.nextInt(sSetOfColors.size()));
    }

    Cluster(Color color) {
        mPoints = new ArrayList<>();
        mColor = color;
    }

    Cluster(ArrayList<Point> points, Color color, float alpha) {
        mPoints = new ArrayList<>();
        add(points);
        mColor = color;
        mColor.alphaSelf(alpha);
    }

    Cluster(Cluster cluster){
        mCenter = new Point(cluster.getCenter());
        mColor = sSetOfColors.get(sRandom.nextInt(sSetOfColors.size()));
        mMaxCoordPoint = new Point(cluster.getMaxCoordPoint());
        mPoints = new ArrayList<>();

        for(Point point : cluster.getPoints())
            mPoints.add(new Point(point));

        /*
        for(Point representativePoint : cluster.getRepresentativePoints())
            mRepresentativePoints.add(new Point(representativePoint));*/
    }

    ArrayList<Point> getRepresentativePoints() {

        if (mRepresentativePoints == null) {
            mRepresentativePoints = new ArrayList<>();
            float minDistanceToMaxCoordPoint = Float.MAX_VALUE;
            Point firstRepresentativePoint = null;
            for (Point point : getPoints()) {
                float distance = point.calculateDistanceTo(getMaxCoordPoint());
                if (distance < minDistanceToMaxCoordPoint) {
                    minDistanceToMaxCoordPoint = distance;
                    firstRepresentativePoint = point;
                }
            }

            if (firstRepresentativePoint != null)
                mRepresentativePoints.add(firstRepresentativePoint);

            while (mRepresentativePoints.size() < Math.ceil(Math.sqrt(mPoints.size()))) {
                Point nextRepresentativePoint = null;
                float maxDistance = 0;
                for (Point point : getPoints()) {
                    if (!mRepresentativePoints.contains(point)) {
                        float sumOfDistances = 0;
                        float maxPenalty = 0f;
                        for (Point representativePoint : mRepresentativePoints) {
                            float penalty = 0f;
                            float distanceToRepresentativePoint = point.calculateDistanceTo(representativePoint);
                            float distanceToCentroid = point.calculateDistanceTo(mCenter);
                            if (distanceToRepresentativePoint < distanceToCentroid)
                                penalty = 1 - distanceToRepresentativePoint / distanceToCentroid;
                            if (penalty > maxPenalty)
                                maxPenalty = penalty;
                            sumOfDistances += distanceToRepresentativePoint;
                        }
                        sumOfDistances = sumOfDistances * (1 - maxPenalty);
                        if (sumOfDistances >= maxDistance) {
                            maxDistance = sumOfDistances;
                            nextRepresentativePoint = point;
                        }
                    }
                }
                mRepresentativePoints.add(nextRepresentativePoint);
            }
            return mRepresentativePoints;
        } else
            return mRepresentativePoints;
    }

    ArrayList<Point> getRepresentativePointsMovedTowardsCentroid(float percentage) {
        ArrayList<Point> movedRepresentativePoints = null;
        if (mRepresentativePoints == null)
            movedRepresentativePoints = getRepresentativePoints();
        else {
            movedRepresentativePoints = new ArrayList<>();
            for (Point representativePoint : mRepresentativePoints) {
                ArrayList<Float> newCoordinates = new ArrayList<>();
                for (int dimension = 0; dimension < representativePoint.getCoordinates().size(); dimension++) {
                    float clusterCentroidCoordinate = getCenter().getCoordinate(dimension);
                    float representativePointCoordinate = representativePoint.getCoordinate(dimension);
                    float distanceToMove = (representativePointCoordinate - clusterCentroidCoordinate) * percentage;
                    float newCoordinate = representativePointCoordinate - distanceToMove;
                    newCoordinates.add(newCoordinate);
                }
                movedRepresentativePoints.add(new Point(newCoordinates));
            }
        }
        return movedRepresentativePoints;
    }

    void setRepresentativePoints(ArrayList<Point> representativePoints) {
        mRepresentativePoints = representativePoints;
    }

    void setRepresentativePoint(int index, Point point) {
        mRepresentativePoints.set(index, point);
    }


    Color getColor() {
        return mColor;
    }

    Color getColorWithAlpha(float alpha) {
        return new Color(getColor().r, getColor().g, getColor().b, alpha);
    }

    void add(ArrayList<Point> points) {
        add(points.toArray(Point[]::new));
    }

    void add(Point... points) {

        // If centroid is null it means no points in the current cluster
        int startIndex;
        if (mCenter == null) {
            mCenter = new Point(points[0]);
            mMaxCoordPoint = new Point(points[0]);
            mPoints.add(points[0]);
            startIndex = 1;
        } else
            startIndex = 0;

        // for each added point
        for (int pointIndex = startIndex; pointIndex < points.length; pointIndex++) {
            // The point coordinates
            ArrayList<Float> pointCoordinates = points[pointIndex].getCoordinates();
            // for each coordinate of the point
            for (int dimension = 0; dimension < pointCoordinates.size(); dimension++) {
                float coordinate = pointCoordinates.get(dimension);
                Float dimensionCenter = Cluster.calculateNewCenter(
                        coordinate,
                        getCenterCoordinate(dimension),
                        mPoints.size());
                if (mMaxCoordPoint.getCoordinate(dimension) < coordinate)
                    mMaxCoordPoint.setCoordinate(dimension, coordinate);
                setCenterCoordinate(dimension, dimensionCenter);
            }
            mPoints.add(points[pointIndex]);
        }
    }


    private static Float calculateNewCenter(float newValue, float oldMean, float oldNumOfElements) {
        return (newValue + oldMean * oldNumOfElements) / (oldNumOfElements + 1);
    }

    Point getCenter() {
        return mCenter;
    }

    private float getCenterCoordinate(int dimensionIndex) {
        return getCenter().getCoordinate(dimensionIndex);
    }

    private void setCenterCoordinate(int dimensionIndex, float value) {
        mCenter.setCoordinate(dimensionIndex, value);
    }

    ArrayList<Point> getPoints() {
        return mPoints;
    }

    @Override
    public String toString() {
        String output = "Cluster centroid: " + getCenter() + " points (" + getPoints().size() + "): " +
                getPoints().stream()
                        .map(Point::toString)
                        .collect(Collectors.joining(", "));
        return output;
    }

    int getNumOfPoints() {
        return getPoints().size();
    }

    public Point getMaxCoordPoint() {
        return mMaxCoordPoint;
    }

    // Returns average distance to centroid
    public float getD() {
        float distanceSum = 0;
        for (Point point : getPoints())
            distanceSum += Math.pow(point.calculateDistanceTo(getCenter()), 2);
        return distanceSum;
    }

    public float getAvgD() {
        float distanceSum = 0;
        for (Point point : getPoints())
            distanceSum += point.calculateDistanceTo(getCenter());
        return distanceSum / getNumOfPoints();
    }

}

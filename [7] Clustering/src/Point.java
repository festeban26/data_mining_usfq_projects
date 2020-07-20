package com.festeban26;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Point {

    private ArrayList<Float> mCoordinates;

    public Point(Point point){
        ArrayList<Float> coordinates = new ArrayList<>();
        for(Float coordinate : point.getCoordinates()){
            float newCoordinate = coordinate;
            coordinates.add(newCoordinate);
        }
        mCoordinates = coordinates;
    }

    public Point(Float... coordinates) {
        mCoordinates = new ArrayList<>();
        mCoordinates.addAll(Arrays.asList(coordinates));
    }

    public Point(ArrayList<Float> coordinates) {
        this(coordinates.toArray(Float[]::new));
    }


    public float calculateDistanceTo(Point point) {
        ArrayList<Float> p1 = mCoordinates;
        ArrayList<Float> p2 = point.getCoordinates();

        if (p1.size() == p2.size()) {
            float sum = 0;
            for (int i = 0; i < mCoordinates.size(); i++)
                sum += Math.pow(p2.get(i) - p1.get(i), 2);
            return (float) Math.sqrt(sum);
        } else {
            System.out.println("Points must be in the same coordinate");
            return 0;
        }
    }

    public ArrayList<Float> getCoordinates() {
        return mCoordinates;
    }

    public float getCoordinate(int dimensionIndex) {
        return getCoordinates().get(dimensionIndex);
    }

    public void setCoordinate(int dimensionIndex, Float value) {
        mCoordinates.set(dimensionIndex, value);
    }

    public void setCoordinates(ArrayList<Float> coordinates) {
        mCoordinates = coordinates;
    }

    public int getDimensionality(){
        return getCoordinates().size();
    }



    @Override
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("0.00");
        StringBuilder output = new StringBuilder();
        output.append("(").append(mCoordinates.stream()
                .map(coordinate -> formatter.format(coordinate))
                .collect(Collectors.joining(", "))).append(")");
        return output.toString();
    }
}

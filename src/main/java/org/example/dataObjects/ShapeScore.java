package org.example.dataObjects;

public class ShapeScore {
    public double aspectRatioScore = 0;
    public double extentScore = 0;

    public double sizeScore = 0;
    public double sum = 0;

    public void setAspectRatioScore(double score) {
        aspectRatioScore = score;
        updateSum();
    }

    public void setExtentScore(double score) {
        extentScore = score;
        updateSum();
    }

    public void setSizeScore(double score) {
        sizeScore = score;
        updateSum();
    }

    private void updateSum() {
        sum = aspectRatioScore + extentScore + sizeScore;
    }
}

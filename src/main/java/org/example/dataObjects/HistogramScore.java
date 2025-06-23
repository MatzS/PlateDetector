package org.example.dataObjects;

/**
 * Enthält die Scores die durch die Vergleiche der
 * Histogramme (HSV) entstanden sind.
 */
public class HistogramScore {
    public double hScore = 0;
    public double sScore = 0;

    public double vScore = 0;

    public double sum;

    public void setHScore(double score) {
        this.hScore = score;
        updateSum();
    }

    public void setVScore(double score) {
        this.vScore = score;
        updateSum();
    }

    public void setSScore(double score) {
        this.sScore = score;
        updateSum();
    }

    public void updateSum() {
        this.sum = this.hScore + this.sScore + this.vScore;
    }
}

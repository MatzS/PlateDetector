package org.example.dataObjects;

public class HierarchyScore {
    public double childCountScore = 0;
    public double childSizeScore = 0;

    public double sum;

    public void setChildCountScore(double score) {
        this.childCountScore = score;
        updateSum();
    }

    public void setChildSizeScore(double score) {
        this.childSizeScore = score;
        updateSum();
    }

    public void updateSum() {
        this.sum = this.childCountScore + this.childSizeScore;
    }
}

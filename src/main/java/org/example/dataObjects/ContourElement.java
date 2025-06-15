package org.example.dataObjects;

import org.example.HistogramUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

public class ContourElement {

    public MatOfPoint contour;

    public HSHistogram hists;
    public HistogramScore histogramScore;
    public HierarchyScore hierarchyScore;

    public ShapeScore shapeScore;

    public double totalScore;

    public ContourElement(MatOfPoint contour) {
        this.contour = contour;
        this.hists = HistogramUtils.calcHSHistogramFromMatOfPoint(contour);
        this.histogramScore = new HistogramScore();
        this.hierarchyScore = new HierarchyScore();
        this.shapeScore = new ShapeScore();
        this.totalScore = 0;
    }
}

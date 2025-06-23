package org.example.dataObjects;

import org.example.HistogramUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

/**
 * Repräsentiert eine im Bild gefundene Kontur.
 * Bündelt alle notwendigen Scores um zu entscheiden,
 * ob es sich bei der Kontur um ein Kennzeichen handelt.
 */
public class ContourElement {

    public MatOfPoint contour;

    public HSVHistogram hists;
    public HistogramScore histogramScore;
    public HierarchyScore hierarchyScore;

    public ShapeScore shapeScore;

    public double totalScore;

    public ContourElement(MatOfPoint contour) {
        this.contour = contour;
        this.hists = HistogramUtils.calcHSVHistogramFromMatOfPoint(contour);
        this.histogramScore = new HistogramScore();
        this.hierarchyScore = new HierarchyScore();
        this.shapeScore = new ShapeScore();
        this.totalScore = 0;
    }
}

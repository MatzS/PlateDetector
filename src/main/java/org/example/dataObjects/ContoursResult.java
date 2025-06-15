package org.example.dataObjects;

import org.example.PlateDetector;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.util.List;

public class ContoursResult {

    public PreprocessingResult ppResult;
    public List<ContourElement> contours;
    public Mat hierarchy;

    private PlateDetector detector;

    public ContoursResult(PreprocessingResult ppResult, List<MatOfPoint> contours, Mat hierarchy) {
        this.ppResult = ppResult;
        this.contours = contours.stream().map(ContourElement::new).toList();
        this.hierarchy = hierarchy;
    }

    public ContourElement getBestContour() {
        return contours.stream().max(java.util.Comparator.comparingDouble(c -> c.totalScore)).orElse(null);
    }

    public List<ContourElement> getNBestContours(int n) {
        return contours.stream()
                .sorted((c1, c2) -> Double.compare(c2.totalScore, c1.totalScore))
                .limit(n)
                .toList();
    }
}

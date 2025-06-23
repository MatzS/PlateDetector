package org.example.dataObjects;

import org.example.PlateDetector;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.util.List;

/**
 * Bündelt alle gefundenen Konturen (mit Hierarchie) in einer Klasse.
 * Enthält außerdem Informationen aus den vorherigen Schritten wie
 * z.B. dem Preprocessing.
 */
public class ContoursResult {

    public PreprocessingResult ppResult;
    public List<ContourElement> contours;
    public Mat hierarchy;

    public ContoursResult(PreprocessingResult ppResult, List<MatOfPoint> contours, Mat hierarchy) {
        this.ppResult = ppResult;
        this.contours = contours.stream().map(ContourElement::new).toList();
        this.hierarchy = hierarchy;
    }

    public ContoursResult(PreprocessingResult ppResult, Mat hierarchy, List<ContourElement> contours) {
        this.ppResult = ppResult;
        this.contours = contours;
        this.hierarchy = hierarchy;
    }


    /**
     * Gibt das ContourElement mit dem besten Score zurück.
     * @return ContourElement mit dem besten Score.
     */
    public ContourElement getBestContour() {
        return contours.stream().max(java.util.Comparator.comparingDouble(c -> c.totalScore)).orElse(null);
    }


    /**
     * Erstellt eine TopN-Liste der gefundenen Konturen.
     * @param n Wieviele Elemente sollen zurückgegeben werden.
     * @return Eine TopN-Liste
     */
    public List<ContourElement> getNBestContours(int n) {
        return contours.stream()
                .sorted((c1, c2) -> Double.compare(c2.totalScore, c1.totalScore))
                .limit(n)
                .toList();
    }
}

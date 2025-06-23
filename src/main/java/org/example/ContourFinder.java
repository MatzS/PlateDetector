package org.example;

import org.example.dataObjects.ContoursResult;
import org.example.dataObjects.PreprocessingResult;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Extrahiert Konturen
 */
public class ContourFinder {

    /**
     * Extrahiert alle Konturen aus den Kanten (edges) der Vorverabeitung
     * @param ppResult Daten der Vorverarbeitung
     * @return Alle Konturen des Bildes mit Hierarchie
     */
    public ContoursResult getContours(PreprocessingResult ppResult) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(ppResult.edges.clone(), contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        return new ContoursResult(ppResult,contours, hierarchy);
    }
}

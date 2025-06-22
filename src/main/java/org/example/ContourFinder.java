package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;
import org.example.dataObjects.PreprocessingResult;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ContourFinder {

    // Verarbeitet das Preprocessing-Result und findet die Contours
    // und die hierarchy. Returned dann alle Daten in einem Contours-Result
    public ContoursResult getContours(PreprocessingResult ppResult) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(ppResult.edges.clone(), contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        return new ContoursResult(ppResult,contours, hierarchy);
    }
}

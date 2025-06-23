package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


/**
 * Filtert Konturen
 */
public class ContoursFilter {

    /**
     * Filtert die Konturen anhand der Konturfläche.
     * @param input Alle Konturen.
     * @param minArea minimale Konturfläche.
     * @param maxArea maximale Konturfläche.
     * @return alle Konturen dessen Konturfläche in den Grenzen liegt.
     */
    public ContoursResult filterContoursByArea(ContoursResult input, double minArea, double maxArea) {
        List<ContourElement> originalElements = input.contours;
        Mat originalHierarchy = input.hierarchy;

        if (originalElements == null || originalElements.isEmpty() || originalHierarchy.empty()) {
            return input;
        }

        List<ContourElement> filteredContours = new ArrayList<>();
        for (ContourElement originalElement : originalElements) {
            MatOfPoint contour = originalElement.contour;
            double area = Imgproc.contourArea(contour);
            if (area >= minArea && area < maxArea) {
                filteredContours.add(originalElement);
            }
        }
        return new ContoursResult(input.ppResult, input.hierarchy, filteredContours);
    }
}

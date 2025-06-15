package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ContoursFilter {
    public ContoursResult filterContoursByArea(ContoursResult input, double minArea) {
        List<ContourElement> originalElements = input.contours;
        Mat originalHierarchy = input.hierarchy;

        if (originalElements == null || originalElements.isEmpty() || originalHierarchy.empty()) {
            return input;
        }

        List<ContourElement> filteredElements = new ArrayList<>();
        List<MatOfPoint> filteredContours = new ArrayList<>();
        List<Integer> indexMapping = new ArrayList<>();

        // 1. Mapping erstellen und filtern
        for (int i = 0; i < originalElements.size(); i++) {
            MatOfPoint contour = originalElements.get(i).contour;
            double area = Imgproc.contourArea(contour);
            if (area >= minArea) {
                indexMapping.add(filteredContours.size());
                filteredContours.add(contour);
                filteredElements.add(originalElements.get(i));
            } else {
                indexMapping.add(-1); // übersprungen
            }
        }

        // 2. Hierarchie neu mappen
        Mat filteredHierarchy = new Mat(filteredContours.size(), 1, originalHierarchy.type());
        int newIdx = 0;
        for (int i = 0; i < originalElements.size(); i++) {
            if (indexMapping.get(i) == -1) continue;

            double[] oldH = originalHierarchy.get(0, i);
            double[] newH = new double[4];
            for (int k = 0; k < 4; k++) {
                int oldRef = (int) oldH[k];
                newH[k] = (oldRef >= 0 && indexMapping.get(oldRef) != -1) ? indexMapping.get(oldRef) : -1;
            }

            filteredHierarchy.put(newIdx++, 0, newH);
        }

        // 3. Neues ContoursResult zurückgeben
        return new ContoursResult(input.ppResult, filteredContours, filteredHierarchy);
    }
}

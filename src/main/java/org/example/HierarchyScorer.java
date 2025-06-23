package org.example;

import org.example.dataObjects.ContoursResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Für die Berechnung der Hierarchie-Scores.
 */
public class HierarchyScorer {

    /**
     * Berechnet für jedes ContourElement den ChildCountScore.
     * Aus der Hierarchie wird ausgelesen, wieviele Kinder-Elemente
     * jede Kontur hat. Anhand der Anzahl der Kinder werden Punkte vergeben.
     * @param result Alle Konturen.
     * @return Alle Konturen mit berechnetem ChildCountScore.
     */
    public  ContoursResult computeChildCountScore(ContoursResult result) {
        Mat hierarchy = result.hierarchy;
        for (int i = 0; i < result.contours.size(); i++) {
            int firstChild = (int) hierarchy.get(0, i)[2];
            int childCount = 0;
            int childIdx = firstChild;

            while (childIdx != -1) {
                childCount++;
                childIdx = (int) hierarchy.get(0, childIdx)[0];
            }

            double score = 0.1;

            if (childCount >= DetectionSettings.HY_NUM_CHILD_LOW && childCount <= DetectionSettings.HY_NUM_CHILD_HIGH) {
                score = DetectionSettings.HY_CHILD_COUNT_SCORE;
            } else if (childCount >= 4 && childCount < 6) {
                score = DetectionSettings.HY_CHILD_COUNT_SCORE * 0.7;
            } else if (childCount >= 2 && childCount < 4) {
                score = DetectionSettings.HY_CHILD_COUNT_SCORE * 0.3;
            } else {
                score = DetectionSettings.HY_CHILD_COUNT_SCORE * 0.1;
            }

            result.contours.get(i).hierarchyScore.setChildCountScore(score);
        }
        return result;
    }

    /**
     * Berechnet den Hierarchie_Score für jede Kontur.
     * @param result Alle Konturen.
     * @return Alle Konturen mit berechnetem Hierarchie-Score
     */
    public ContoursResult computeHierarchyScores(ContoursResult result) {
        ContoursResult afterCount = this.computeChildCountScore(result);
        return afterCount;
    }
}

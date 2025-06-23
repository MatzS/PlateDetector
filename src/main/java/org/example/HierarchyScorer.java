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
     * Untersucht für jede Kontur die Höhe der Child-Elemente. Wenn die
     * Children immer ähnlich groß sind, dann gibt es einen besseren Score.
     * Weil bei einem Kennzeichen die Zeichen ja meinst gleich groß sind.
     * @param result Alle Konturen
     * @return Alle Konturen mit berechnetem Score.
     */
    public ContoursResult computeSimilarSizeChildrenScore(ContoursResult result) {
        Mat hierarchy = result.hierarchy;

        for (int i = 0; i < result.contours.size(); i++) {
            int firstChild = (int) hierarchy.get(0, i)[2];
            if (firstChild == -1) continue;

            List<Double> heights = new ArrayList<>();
            int childIdx = firstChild;

            while (childIdx != -1) {
                RotatedRect rr = Imgproc.minAreaRect(new MatOfPoint2f(result.contours.get(childIdx).contour.toArray()));
                double height = Math.max(rr.size.height, rr.size.width);
                if (height > 5) heights.add(height);

                childIdx = (int) hierarchy.get(0, childIdx)[0];
            }

            if (heights.size() < 2) continue;

            double avg = heights.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double stdDev = Math.sqrt(
                    heights.stream().mapToDouble(h -> (h - avg) * (h - avg)).average().orElse(0)
            );
            double variationRatio = stdDev / avg;

            double score = Math.max(0.1, 1.5 - variationRatio);

            // kleiner Boost bei vielen ähnlichen Zeichen
            if (heights.size() >= 5 && variationRatio < 0.3) {
                score += 0.1;
            }

            result.contours.get(i).hierarchyScore.setChildSizeScore(Math.max(score, 0.1));
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
        ContoursResult afterSize = this.computeSimilarSizeChildrenScore(afterCount);
        return afterSize;
    }
}

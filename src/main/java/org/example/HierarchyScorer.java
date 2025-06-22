package org.example;

import org.example.dataObjects.ContoursResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HierarchyScorer {

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
            if(i > 160) {
                System.out.println("!");
            }
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

    public ContoursResult computeSimilarSizeChildrenScore(ContoursResult result) {
        Mat hierarchy = result.hierarchy;

        for (int i = 0; i < result.contours.size(); i++) {
            int firstChild = (int) hierarchy.get(0, i)[2];
            if (firstChild == -1) continue;

            List<Double> heights = new ArrayList<>();
            int childIdx = firstChild;

            while (childIdx != -1) {
                RotatedRect rr = Imgproc.minAreaRect(new MatOfPoint2f(result.contours.get(childIdx).contour.toArray()));
                double height = Math.max(rr.size.height, rr.size.width); // Höhe unabhängig von Rotation
                if (height > 5) heights.add(height); // kleine Fragmente ignorieren

                childIdx = (int) hierarchy.get(0, childIdx)[0]; // next sibling
            }

            if (heights.size() < 2) continue;

            double avg = heights.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double stdDev = Math.sqrt(
                    heights.stream().mapToDouble(h -> (h - avg) * (h - avg)).average().orElse(0)
            );
            double variationRatio = stdDev / avg;

            double score = Math.max(0.1, 1.5 - variationRatio); // weniger Abweichung → höherer Score

            // kleiner Boost bei vielen ähnlichen Zeichen
            if (heights.size() >= 5 && variationRatio < 0.3) {
                score += 0.1;
            }

            result.contours.get(i).hierarchyScore.setChildSizeScore(Math.max(score, 0.1));
        }

        return result;
    }


    public ContoursResult computeHierarchyScores(ContoursResult result) {
        ContoursResult afterCount = this.computeChildCountScore(result);
        ContoursResult afterSize = this.computeSimilarSizeChildrenScore(afterCount);
        return afterCount;
    }
}

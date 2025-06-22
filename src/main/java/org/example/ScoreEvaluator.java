package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;

public class ScoreEvaluator {

    // Histogram weights
    private static final double WEIGHT_H = 0.15;
    private static final double WEIGHT_S = 0.15;
    private static final double WEIGHT_V = 0.15;

    // Shape weights
    private static final double WEIGHT_ASPECT = 0.25;
    private static final double WEIGHT_EXTENT = 0.20;

    // Hierarchy weights
    private static final double WEIGHT_CHILD_COUNT = 0.25;
    private static final double WEIGHT_CHILD_SIZE = 0.15;

    public ContoursResult computeTotalScores(ContoursResult result) {
        for (ContourElement ce : result.contours) {
            double totalScore = 0;

            // Histogram
            totalScore += ce.histogramScore.hScore * WEIGHT_H;
            totalScore += ce.histogramScore.sScore * WEIGHT_S;
            totalScore += ce.histogramScore.vScore * WEIGHT_V;

            // Shape
            totalScore += ce.shapeScore.aspectRatioScore * WEIGHT_ASPECT;
            totalScore += ce.shapeScore.extentScore * WEIGHT_EXTENT;

            // Hierarchy
            totalScore += ce.hierarchyScore.childCountScore * WEIGHT_CHILD_COUNT;
            totalScore += ce.hierarchyScore.childSizeScore * WEIGHT_CHILD_SIZE;

            ce.totalScore = (ce.histogramScore.sum * 0.7)
                            * ce.hierarchyScore.childSizeScore * ce.hierarchyScore.childCountScore
                            * ce.shapeScore.extentScore * ce.shapeScore.aspectRatioScore;
        }
        return result;
    }

    public static void checkAllZeroScores(ContoursResult result) {
        boolean allH = result.contours.stream().allMatch(c -> c.histogramScore.hScore == 0.0);
        boolean allS = result.contours.stream().allMatch(c -> c.histogramScore.sScore == 0.0);
        boolean allV = result.contours.stream().allMatch(c -> c.histogramScore.vScore == 0.0);

        boolean allChildCount = result.contours.stream().allMatch(c -> c.hierarchyScore.childCountScore == 0.0);
        boolean allChildSize = result.contours.stream().allMatch(c -> c.hierarchyScore.childSizeScore == 0.0);

        boolean allAspectRatio = result.contours.stream().allMatch(c -> c.shapeScore.aspectRatioScore == 0.0);
        boolean allExtent = result.contours.stream().allMatch(c -> c.shapeScore.extentScore == 0.0);

        boolean allTotal = result.contours.stream().allMatch(c -> c.totalScore == 0.0);

        System.out.println("Alle Histogram hScore = 0: " + allH);
        System.out.println("Alle Histogram sScore = 0: " + allS);
        System.out.println("Alle Histogram vScore = 0: " + allV);

        System.out.println("Alle Hierarchy childCountScore = 0: " + allChildCount);
        System.out.println("Alle Hierarchy childSizeScore = 0: " + allChildSize);

        System.out.println("Alle Shape aspectRatioScore = 0: " + allAspectRatio);
        System.out.println("Alle Shape extentScore = 0: " + allExtent);

        System.out.println("Alle totalScore = 0: " + allTotal);
    }
}

package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

/**
 * Für die Berechnung der Shape-Scores
 */
public class ShapeScorer {

    /**
     * Gibt das minAreaRect der Kontur zurück.
     * @param contour Kontur
     * @return minAreaRect
     */
    private RotatedRect getRotatedRect(MatOfPoint contour) {
        MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
        return Imgproc.minAreaRect(contour2f);
    }

    /**
     * Berechnet das Seitenverhältnis
     * @param rect minAreaRect
     * @return Seitenverhältnis
     */
    private double computeAspectRatio(RotatedRect rect) {
        double width = rect.size.width;
        double height = rect.size.height;
        if (height == 0 || width == 0) return 0;
        return width > height ? width / height : height / width;
    }

    /**
     * Berechnet den "Extent" der Kontur. Dazu nehmen wir das RotatedRect, was
     * mit Imgproc.minAreaRect(contour2f) erstellt wurde und vergleichen dann die
     * Flächen der beiden Elemente. Weil ein Kennzeichen rechteckig ist, sollten die
     * Flächen sehr ähnlich sein.
     * @param contour Kontur
     * @param rect minAreaRect der Kontur
     * @return Verhältnis aus Kontur- / Rect-Fläche.
     */
    private double computeExtent(MatOfPoint contour, RotatedRect rect) {
        double contourArea = Imgproc.contourArea(contour);
        double rectArea = rect.size.width * rect.size.height;
        if (rectArea == 0) return 0;
        return contourArea / rectArea;
    }

    /**
     * Bewertet das Seitenverhältnis
     * @param aspectRatio Seitenverhältnis
     * @return Sitenverhältnis-Score
     */
    private double scoreAspectRatio(double aspectRatio) {
        if (aspectRatio >= DetectionSettings.SH_ASPECT_LOWER && aspectRatio <= DetectionSettings.SH_ASPECT_HIGHER) return DetectionSettings.SH_ASPECT_FACTOR;
        if (aspectRatio >= 2.8 && aspectRatio < 5.5) return DetectionSettings.SH_ASPECT_FACTOR * 0.5;
        return 0;
    }

    /**
     * Bewertet den Extent
     * @param extent Extent-Wert
     * @return Extent-Score
     */
    private double scoreExtent(double extent) {
        double target = DetectionSettings.SH_EXTENT;
        double factor = DetectionSettings.SH_EXTENT_FACTOR;

        if (extent >= target) return factor;

        double diff = target - extent;
        double normalized = diff / target;

        double p = 0.4;
        double score = factor * (1 - Math.pow(normalized, p));

        return Math.max(0, score);
    }

    /**
     * Berechnet und bewertet die Größe der Kontur
     * @param ce Kontur
     * @return Shape-Score der Kontur
     */
    private double scoreSize(ContourElement ce) {
        MatOfPoint2f contour2f = new MatOfPoint2f(ce.contour.toArray());
        RotatedRect rotatedRect = Imgproc.minAreaRect(contour2f);
        double area = rotatedRect.size.height * rotatedRect.size.width;
        if(area > DetectionSettings.SH_SIZE_HIGHEST) {
            return 1.0;
        } else if(area > DetectionSettings.SH_SIZE_MIDDLE) {
            return 0.7;
        } else if(area > DetectionSettings.SH_SIZE_LOWEST) {
            return 0.5;
        } else {
            return 0;
        }
    }

    /**
     * Berechnet für jede Kontur den Shape-Score
     * @param result Alle Konturen
     * @return Alle Konturen mit gesetztem Shape-Score.
     */
    public ContoursResult computeShapeScores(ContoursResult result) {
        for (ContourElement ce : result.contours) {
            RotatedRect rect = getRotatedRect(ce.contour);
            double aspectRatio = computeAspectRatio(rect);
            double extent = computeExtent(ce.contour, rect);

            double aspectRatioScore = scoreAspectRatio(aspectRatio);
            double extentScore = scoreExtent(extent);
            double sizeScore = scoreSize(ce);

            ce.shapeScore.setAspectRatioScore(aspectRatioScore);
            ce.shapeScore.setExtentScore(extentScore);
            ce.shapeScore.setSizeScore(sizeScore);
        }
        return result;
    }
}

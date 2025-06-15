package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;


public class ShapeScorer {

    private RotatedRect getRotatedRect(MatOfPoint contour) {
        MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
        return Imgproc.minAreaRect(contour2f);
    }

    private double computeAspectRatio(RotatedRect rect) {
        double width = rect.size.width;
        double height = rect.size.height;
        if (height == 0 || width == 0) return 0;
        return width > height ? width / height : height / width;
    }

    private double computeExtent(MatOfPoint contour, RotatedRect rect) {
        double contourArea = Imgproc.contourArea(contour);
        double rectArea = rect.size.width * rect.size.height;
        if (rectArea == 0) return 0;
        return contourArea / rectArea;
    }

    private double scoreAspectRatio(double aspectRatio) {
        if (aspectRatio >= DetectionSettings.SH_ASPECT_LOWER && aspectRatio <= DetectionSettings.SH_ASPECT_HIGHER) return DetectionSettings.SH_ASPECT_FACTOR;
        if (aspectRatio >= 2.8 && aspectRatio < 5.5) return DetectionSettings.SH_ASPECT_FACTOR * 0.5;
        return 0;
    }

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

    private double scoreSize(ContourElement ce) {
        MatOfPoint2f contour2f = new MatOfPoint2f(ce.contour.toArray());
        RotatedRect rotatedRect = Imgproc.minAreaRect(contour2f);
        double area = rotatedRect.size.height * rotatedRect.size.width;
        if(area > 2000) {
            return 1.0;
        } else if(area > 1000) {
            return 0.7;
        } else if(area > 800) {
            return 0.5;
        } else {
            return 0;
        }
    }

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

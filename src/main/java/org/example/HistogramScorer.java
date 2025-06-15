package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;
import org.example.dataObjects.HSHistogram;
import org.example.dataObjects.HistogramScore;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class HistogramScorer {
    private final HSHistogram referenceHistogram;

    public HistogramScorer(String refFolderpath) {
        this.referenceHistogram = HistogramUtils.calcAverageHSHistogram(refFolderpath);
    }

    public ContoursResult computeHistogramScores(ContoursResult result) {
        for(ContourElement contour : result.contours) {
            scoreHistogram(contour);
        }
        return result;
    }

    private void scoreHistogram(ContourElement element) {
        HistogramScore score = new HistogramScore();
        score.setHScore(compareHist(referenceHistogram.histH, element.hists.histH) * DetectionSettings.HI_HIST_FAKTOR_H);
        score.setSScore(compareHist(referenceHistogram.histS, element.hists.histS) * DetectionSettings.HI_HIST_FAKTOR_S);
        score.setVScore(compareHist(referenceHistogram.histV, element.hists.histV) * DetectionSettings.HI_HIST_FAKTOR_V);
        element.histogramScore = score;
    }

    private double compareHist(Mat ref, Mat candidate) {
        double similarity = Imgproc.compareHist(ref, candidate, Imgproc.HISTCMP_CORREL);
        return Math.max(0, (similarity + 1.0) / 2.0);
    }
}

package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;
import org.example.dataObjects.HSVHistogram;
import org.example.dataObjects.HistogramScore;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Für die Berechnung der Histogram-Scores.
 */
public class HistogramScorer {
    private final HSVHistogram referenceHistogram;

    /**
     * Im Konstruktor wird aus den Bildern im Referenz-Kennzeichen-Ordner
     * ein Referenz-Histogramm erstellt.
     * @param refFolderpath Pfad zum Ordner mit den Referenz-Kennzeichen
     */
    public HistogramScorer(String refFolderpath) {
        this.referenceHistogram = HistogramUtils.calcAverageHSVHistogram(refFolderpath);
    }

    /**
     * Berechnet für jedes Kontur den HistogramScore
     * @param result Alle Konturen.
     * @return Alle Konturen mit HistogramScore.
     */
    public ContoursResult computeHistogramScores(ContoursResult result) {
        for(ContourElement contour : result.contours) {
            scoreHistogram(contour);
        }
        return result;
    }

    /**
     * Berechnet den HistogramScore für ein ContourElement
     * @param element ContourElement
     */
    private void scoreHistogram(ContourElement element) {
        HistogramScore score = new HistogramScore();
        score.setHScore(compareHist(referenceHistogram.histH, element.hists.histH) * DetectionSettings.HI_HIST_FAKTOR_H);
        score.setSScore(compareHist(referenceHistogram.histS, element.hists.histS) * DetectionSettings.HI_HIST_FAKTOR_S);
        score.setVScore(compareHist(referenceHistogram.histV, element.hists.histV) * DetectionSettings.HI_HIST_FAKTOR_V);
        element.histogramScore = score;
    }

    /**
     * Vergleicht zwei Histogramme
     * @param ref Referenz-Histogramm
     * @param candidate Kandidaten-Histogramm
     * @return Score
     */
    private double compareHist(Mat ref, Mat candidate) {
        double similarity = Imgproc.compareHist(ref, candidate, Imgproc.HISTCMP_CORREL);
        return Math.max(0, (similarity + 1.0) / 2.0);
    }
}

package org.example.dataObjects;

import org.opencv.core.Mat;

/**
 * Bündelt die Histogramm-Daten für alle drei Kanäle
 * eines Bildes im HSV-Farbmodell
 */
public class HSVHistogram {
    public Mat histH;
    public Mat histS;
    public Mat histV;

    public HSVHistogram(Mat histH, Mat histS, Mat histV) {
        this.histH = histH;
        this.histS = histS;
        this.histV = histV;
    }
}

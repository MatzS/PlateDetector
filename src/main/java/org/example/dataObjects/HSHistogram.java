package org.example.dataObjects;

import org.opencv.core.Mat;

// Repräsentiert zwei Histogramme im HSV-Farbraum
// Einmal fpr den H- und den S-Wert
public class HSHistogram {
    public Mat histH;
    public Mat histS;
    public Mat histV;

    public HSHistogram(Mat histH, Mat histS, Mat histV) {
        this.histH = histH;
        this.histS = histS;
        this.histV = histV;
    }
}

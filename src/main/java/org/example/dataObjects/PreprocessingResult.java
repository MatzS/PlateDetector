package org.example.dataObjects;

import org.opencv.core.Mat;

/**
 * Das Ergebnis der Vorverarbeitung des Bildes
 * Es beinhaltet das Bild in Graustufen, die gefunden Kanten und
 * das geblurrte Bild.
 */
public class PreprocessingResult {
    public Mat grey;
    public Mat edges;
    public Mat blurred;

    public PreprocessingResult(Mat grey, Mat edges, Mat blurred) {
        this.grey = grey;
        this.edges = edges;
        this.blurred = blurred;
    }
}

package org.example.dataObjects;

import org.opencv.core.Mat;

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

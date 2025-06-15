package org.example.dataObjects;

import org.opencv.core.Mat;

public class PreprocessingResult {
    public Mat grey;
    public Mat edges;

    public Mat blurred;

    public PreprocessingSetting setting;

    public PreprocessingResult(Mat grey, Mat edges, Mat blurred, PreprocessingSetting setting) {
        this.grey = grey;
        this.edges = edges;
        this.blurred = blurred;
        this.setting = setting;
    }

    public PreprocessingResult(Mat grey, Mat edges, Mat blurred) {
        this.grey = grey;
        this.edges = edges;
        this.blurred = blurred;
        this.setting = null;
    }
}

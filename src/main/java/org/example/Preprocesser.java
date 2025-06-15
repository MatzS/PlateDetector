package org.example;

import org.example.dataObjects.PreprocessingResult;
import org.example.dataObjects.PreprocessingSetting;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Preprocesser {

    // Wandelt das Bild in ein Graustufen-Bild um
    // Glättet dann das Bild mit einem Gaus-Filter
    // Und extrahiert die Kanten mit einem Canny-Filter
    public PreprocessingResult preprocessingImage() {
        Mat gray = new Mat();
        Imgproc.cvtColor(PlateDetector.src, gray, Imgproc.COLOR_BGR2GRAY);

        Mat mask = new Mat();
        Imgproc.threshold(gray, mask, 150, 255, Imgproc.THRESH_BINARY);

        gray.setTo(new Scalar(255), mask);

        Mat blurred = new Mat();
        Imgproc.GaussianBlur(mask, blurred, DetectionSettings.PRE_GAUS_SIZE, 0);

        Mat edges = new Mat();
        Imgproc.Canny(mask, edges, DetectionSettings.PRE_CANNY1, DetectionSettings.PRE_CANNY2);

        Imgproc.dilate(edges, edges, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
        return new PreprocessingResult(mask, edges, blurred);
    }
}

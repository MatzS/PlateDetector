package org.example;

import org.example.dataObjects.PreprocessingResult;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Kümmert sich um die Vorverarbeitung des Bildes.
 */
public class Preprocesser {

    /**
     * Vorverarbeitung des Bildes
     * @return Ergebniss nach der Vorverabeitung
     */
    public PreprocessingResult preprocessingImage() {
        Mat gray = new Mat();
        Imgproc.cvtColor(PlateDetector.src, gray, Imgproc.COLOR_BGR2GRAY);
        Visualizer.drawMat(gray, "Bilder/Pres/gray.jpg");
        Mat mask = new Mat();
        Imgproc.threshold(gray, mask, 150, 255, Imgproc.THRESH_BINARY);
        Visualizer.drawMat(mask, "Bilder/Pres/mask.jpg");
        gray.setTo(new Scalar(255), mask);

        Mat blurred = new Mat();
        Imgproc.GaussianBlur(mask, blurred, DetectionSettings.PRE_GAUS_SIZE, 0);

        Mat edges = new Mat();
        Imgproc.Canny(mask, edges, DetectionSettings.PRE_CANNY1, DetectionSettings.PRE_CANNY2);
        Visualizer.drawMat(edges, "Bilder/Pres/edge_ohne_dilate.jpg");

        // Kanten verstärken
        Imgproc.dilate(edges, edges, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
        Visualizer.drawMat(edges, "Bilder/Pres/edge_mit_dilate.jpg");

        return new PreprocessingResult(mask, edges, blurred);
    }
}

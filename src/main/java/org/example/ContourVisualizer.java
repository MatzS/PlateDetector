package org.example;

import org.example.dataObjects.ContourElement;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ContourVisualizer {
    public static void drawContour(ContourElement bestContour) {
        Mat output = PlateDetector.src.clone();
        Imgproc.drawContours(output, java.util.Collections.singletonList(bestContour.contour), -1, new Scalar(0, 255, 0), 2);

        // Optional: Bild anzeigen
        HighGui.imshow("Beste Kontur", output);
        HighGui.waitKey();

        // Optional: Bild speichern
        Imgcodecs.imwrite("Bilder/beste_kontur.jpg", PlateDetector.src.clone());
    }
}

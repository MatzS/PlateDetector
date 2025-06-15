package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.List;

public class Visualizer {
    public static void drawContour(ContourElement bestContour, String filename) {
        Rect rect = Imgproc.boundingRect(bestContour.contour);
        Mat src = PlateDetector.src.clone();
        if (rect.x >= 0 && rect.y >= 0 &&
                rect.x + rect.width <= src.cols() &&
                rect.y + rect.height <= src.rows()) {

            Mat roi = new Mat(src, rect);
            Imgcodecs.imwrite(filename, roi);
        }
    }

    public static void drawNContours(List<ContourElement> contours, String folderPath) {
        for (int i = 0; i < contours.size(); i++) {
            ContourElement cont = contours.get(i);
            drawContour(cont, folderPath + "/prediction_" +  i + ".jpg");
        }
    }

    public static void drawMat(Mat mat, String filename) {
        Imgcodecs.imwrite(filename, mat);
    }

    public static void drawAllContours(ContoursResult result) {
        deleteOldContours();
        for (int i = 0; i < result.contours.size(); i++) {
            ContourElement cont = result.contours.get(i);
            drawContour(cont, "Bilder/Konturen/contour_" +  i + ".jpg");
        }
    }

    private static void deleteOldContours() {
        File dir = new File("Bilder/Konturen");
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        } else {
            dir.mkdirs();
        }
    }
}

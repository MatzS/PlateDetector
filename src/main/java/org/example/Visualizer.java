package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.List;

/**
 * Methoden für die Visualisierung/Speicherung von Elementen/Matrizen.
 */
public class Visualizer {

    /**
     * Speichert die Bounding-Box der Kontur im File.
     * 
     * @param bestContour Kontur
     * @param filename    Filename/Pfad.
     */
    public static void drawContour(ContourElement bestContour, String filename) {
        Rect rect = Imgproc.boundingRect(bestContour.contour);
        Mat src = PlateDetector.src.clone();
        if (rect.x >= 0 && rect.y >= 0 && rect.x + rect.width <= src.cols() && rect.y + rect.height <= src.rows()) {

            Mat roi = new Mat(src, rect);
            Imgcodecs.imwrite(filename, roi);
        }
    }

    /**
     * Speichert eine Liste von Konturen im angegebenen Pfad.
     * 
     * @param contours   Konturen
     * @param folderPath Pfad
     */
    public static void drawNContours(List<ContourElement> contours, String folderPath) {
        for (int i = 0; i < contours.size(); i++) {
            ContourElement cont = contours.get(i);
            drawContour(cont, folderPath + "/prediction_" + i + ".jpg");
        }
    }

    /**
     * Speichert/Zeichnet Matrix ins angegebene File.
     * 
     * @param mat      Matrix
     * @param filename File/Pfad
     */
    public static void drawMat(Mat mat, String filename) {
        Imgcodecs.imwrite(filename, mat);
    }

    /**
     * Zeichnet alle Konturen in den Bilder/Konturen-Ordner
     * 
     * @param result Alle Konturen
     */
    public static void drawAllContours(ContoursResult result) {
        deleteOldContours();
        for (int i = 0; i < result.contours.size(); i++) {
            ContourElement cont = result.contours.get(i);
            drawContour(cont, "Bilder/Konturen/contour_" + i + ".jpg");
        }
    }

    /**
     * Löscht alle alten Konturen im Bilder/Konturen-Ordner
     */
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

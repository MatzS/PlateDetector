package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Liest die Zeichen von einem Autokennzeichen.
 */
public class PlateReader {
    private PlateCharacterTemplates pct;
    private Mat licensePlate;

    /**
     * Erstellt einen neuen Reader für das angegebene Autokennzeichen.
     * 
     * @param licensePlate Ein Bild von einem Kennzeichen als Graustufenbild.
     */
    public PlateReader(Mat licensePlate) {
        pct = new PlateCharacterTemplates();
        this.licensePlate = licensePlate;
    }

    /**
     * Liest das Autokennzeichen und gibt den Text zurück.
     * 
     * @return Ein String, der das Kennzeichen repräsentiert.
     */
    public String readPlateText() {
        Imgproc.threshold(licensePlate, licensePlate, 127, 255, Imgproc.THRESH_BINARY_INV);

        List<MatOfPoint> contours = findContours();

        String plateText = extractTextFromContours(contours);
        return "Erkanntes Kennzeichen: " + plateText;
    }

    private List<MatOfPoint> findContours() {
        // Finde Konturen
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(licensePlate.clone(), contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // Konturen nach x-Position sortieren
        contours.sort(Comparator.comparing(cnt -> Imgproc.boundingRect(cnt).x));
        return contours;
    }

    private String extractTextFromContours(List<MatOfPoint> contours) {
        String plateText = "";
        int contourCounter = 0;
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            if (isPlateCharacter(rect)) {
                contourCounter++;
                Mat roi = new Mat(licensePlate, rect);
                Imgcodecs.imwrite("Bilder/Konturen/Kontur" + contourCounter + ".jpg", roi);

                plateText += findBestMatch(roi);
            }
        }
        return plateText;
    }

    private static boolean isPlateCharacter(Rect r) {
        double aspectRatio = (double) r.width / r.height;
        return 0.45 < aspectRatio && aspectRatio < 1.5 && r.height > 50;
    }

    private String findBestMatch(Mat roi) {
        String bestMatch = "?";
        double bestScore = Double.MAX_VALUE;
        for (Map.Entry<String, Mat> entry : pct.templates.entrySet()) {
            Mat template = entry.getValue();
            Mat resizedRoi = new Mat();
            Imgproc.resize(roi, resizedRoi, template.size());

            Mat result = new Mat();
            Imgproc.matchTemplate(resizedRoi, template, result, Imgproc.TM_SQDIFF);
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            if (mmr.minVal < bestScore) {
                bestScore = mmr.minVal;
                bestMatch = entry.getKey();
            }
        }
        return bestMatch;
    }

}

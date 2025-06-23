package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Stellt Bilder Templates für die Zeichen auf Autokennzeichen zur Verfügung.
 * <p>
 * Die Templates werden aus einem vordefinierten Alphabet-Bild generiert.
 * </p>
 */
public class PlateCharacterTemplates {
    private Mat alphabet;
    public Map<String, Mat> templates;

    /**
     * Erstellt neue Templates für Zeichen, indem ein festes Alphabet-Bild ausgelesen wird.
     * <p>
     * Templates werden in einer Map gespeichert mit den entsprechenden Zeichen als Keys.
     * </p>
     */
    public PlateCharacterTemplates() {
        alphabet = Imgcodecs.imread("Bilder/Kennzeichen_Alphabet.jpg", Imgcodecs.IMREAD_GRAYSCALE);
        Imgproc.threshold(alphabet, alphabet, 127, 255, Imgproc.THRESH_BINARY_INV);
        templates = new HashMap<>();

        generateTemplates();
    }

    private void generateTemplates() {
        List<MatOfPoint> contours = findContours();
        List<Rect> rects = getBoundingRects(contours);

        List<List<Rect>> rows = splitIntoRows(rects);

        matchWithChars(rows);
    }

    private List<MatOfPoint> findContours() {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(alphabet.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    private List<Rect> getBoundingRects(List<MatOfPoint> contours) {
        List<Rect> boundingRects = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            boundingRects.add(Imgproc.boundingRect(contour));
        }

        List<Rect> mergedRects = mergeUmlautRects(boundingRects);

        return mergedRects;
    }

    private List<Rect> mergeUmlautRects(List<Rect> boundingRects) {
        List<Rect> mergedRects = new ArrayList<>();
        for (Rect rect : boundingRects) {
            boolean isMerged = false;
            for (int i = 0; i < mergedRects.size(); i++) {
                Rect existingRect = mergedRects.get(i);
                if (rectsAreClose(existingRect, rect)) {
                    mergedRects.set(i, mergeRects(existingRect, rect));
                    isMerged = true;
                    break;
                }
            }
            if (!isMerged) {
                mergedRects.add(rect);
            }
        }

        // Entferne übrig gebliebene Umlaut-Punkte
        mergedRects.removeIf(r -> r.width * r.height < 150);
        return mergedRects;
    }

    private boolean rectsAreClose(Rect r1, Rect r2) {
        boolean horizontal = Math.abs(r1.x - r2.x) < 10;
        boolean vertical = Math.abs(r1.y - r2.y) < 40;
        return horizontal && vertical;
    }

    private Rect mergeRects(Rect r1, Rect r2) {
        int x = Math.min(r1.x, r2.x);
        int y = Math.min(r1.y, r2.y);
        int x2 = Math.max(r1.x + r1.width, r2.x + r2.width);
        int y2 = Math.max(r1.y + r1.height, r2.y + r2.height);
        int width = x2 - x;
        int height = y2 - y;
        return new Rect(x, y, width, height);
    }

    private List<List<Rect>> splitIntoRows(List<Rect> rects) {
        double rowThreshold = 20;
        List<List<Rect>> rows = new ArrayList<>();
        rects.sort(Comparator.comparingInt(r -> r.y));
        for (Rect rect : rects) {
            boolean isAdded = false;
            for (List<Rect> row : rows) {
                if (Math.abs(row.get(0).y - rect.y) < rowThreshold) {
                    row.add(rect);
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded) {
                List<Rect> newRow = new ArrayList<>();
                newRow.add(rect);
                rows.add(newRow);
            }
        }

        // Jede Zeile nach x-Position sortieren
        for (List<Rect> row : rows) {
            row.sort(Comparator.comparingInt(r -> r.x));
        }
        return rows;
    }

    private void matchWithChars(List<List<Rect>> rows) {
        String[] charRows = { "ABCDEFGHIJKLM", "NOPQRSTUVWXYZ", "ÄÖÜ1234567890" };
        for (int i = 0; i < rows.size(); i++) {
            List<Rect> row = rows.get(i);
            String chars = charRows[i];
            for (int j = 0; j < row.size() && j < chars.length(); j++) {
                Rect rect = row.get(j);
                Mat roi = new Mat(alphabet, rect);
                templates.put(String.valueOf(chars.charAt(j)), roi);

                Imgcodecs.imwrite("Bilder/Templates/Template" + chars.charAt(j) + ".jpg", roi);
            }
        }
    }
}

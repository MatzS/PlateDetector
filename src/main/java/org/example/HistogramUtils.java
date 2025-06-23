package org.example;

import org.example.dataObjects.HSVHistogram;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Collections;

/**
 * Hilfsfunktionen für die Arbeit mit den Histogrammen
 */
public class HistogramUtils {

    /**
     * Erstellt aus einer Kontur ein HSVHistogramm
     * 
     * @param contour Kontur
     * @return ein HSVHistogramm
     */
    public static HSVHistogram calcHSVHistogramFromMatOfPoint(MatOfPoint contour) {
        Rect rect = Imgproc.boundingRect(contour);
        Mat hsv = new Mat(PlateDetector.hsv, rect);

        Mat mask = Mat.zeros(rect.size(), CvType.CV_8UC1);
        Point[] points = contour.toArray();
        for (int i = 0; i < points.length; i++) {
            points[i].x -= rect.x;
            points[i].y -= rect.y;
        }
        MatOfPoint shifted = new MatOfPoint(points);
        Imgproc.drawContours(mask, Collections.singletonList(shifted), -1, new Scalar(255), -1);

        Mat histH = new Mat();
        Imgproc.calcHist(Collections.singletonList(hsv), new MatOfInt(0), mask, histH, new MatOfInt(180),
                new MatOfFloat(0f, 180f));
        Core.normalize(histH, histH);

        Mat histS = new Mat();
        Imgproc.calcHist(Collections.singletonList(hsv), new MatOfInt(1), mask, histS, new MatOfInt(256),
                new MatOfFloat(0f, 256f));
        Core.normalize(histS, histS);

        Mat histV = new Mat();
        Imgproc.calcHist(Collections.singletonList(hsv), new MatOfInt(2), mask, histV, new MatOfInt(256),
                new MatOfFloat(0f, 256f));
        Core.normalize(histV, histV);

        return new HSVHistogram(histH, histS, histV);
    }

    /**
     * Erstellt aus den Bildern im folderpath ein Referenz-Histogramm.
     * 
     * @param folderpath Pfad zum Kennzeichen-Ordner
     * @return Referenz-HSVHistogramm
     */
    public static HSVHistogram calcAverageHSVHistogram(String folderpath) {
        File folder = new File(folderpath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (files == null || files.length == 0) {
            System.out.println("Keine JPG-Dateien im Ordner gefunden: " + folderpath);
            return null;
        }

        Mat avgHistH = Mat.zeros(180, 1, CvType.CV_32F);
        Mat avgHistS = Mat.zeros(256, 1, CvType.CV_32F);
        Mat avgHistV = Mat.zeros(256, 1, CvType.CV_32F);
        int count = 0;

        for (File file : files) {
            Mat img = Imgcodecs.imread(file.getAbsolutePath());
            if (!img.empty()) {
                Mat hsv = new Mat();
                Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV);

                Mat histH = new Mat();
                Imgproc.calcHist(Collections.singletonList(hsv), new MatOfInt(0), new Mat(), histH, new MatOfInt(180),
                        new MatOfFloat(0f, 180f));
                Core.normalize(histH, histH);

                Mat histS = new Mat();
                Imgproc.calcHist(Collections.singletonList(hsv), new MatOfInt(1), new Mat(), histS, new MatOfInt(256),
                        new MatOfFloat(0f, 256f));
                Core.normalize(histS, histS);

                Mat histV = new Mat();
                Imgproc.calcHist(Collections.singletonList(hsv), new MatOfInt(2), new Mat(), histV, new MatOfInt(256),
                        new MatOfFloat(0f, 256f));
                Core.normalize(histV, histV);

                Core.add(avgHistH, histH, avgHistH);
                Core.add(avgHistS, histS, avgHistS);
                Core.add(avgHistV, histV, avgHistV);
                count++;
            } else {
                System.out.println("Fehler beim Laden: " + file.getName());
            }
        }

        if (count > 0) {
            Core.divide(avgHistH, new Scalar(count), avgHistH);
            Core.divide(avgHistS, new Scalar(count), avgHistS);
            Core.divide(avgHistV, new Scalar(count), avgHistV);
        }

        return new HSVHistogram(avgHistH, avgHistS, avgHistV);
    }
}

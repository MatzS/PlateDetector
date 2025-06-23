package org.example;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {
    public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        testSingleImage(Imgcodecs.imread("Bilder/Cars/car05.jpg"));
    }

    public static void testSingleImage(Mat image) {
        PlateDetector detector = new PlateDetector(image);
        Mat result = detector.detectPlate();
        Mat grey = new Mat();
        Imgproc.cvtColor(result, grey, Imgproc.COLOR_BGR2GRAY);
        PlateReader reader = new PlateReader(grey);

        String plate = reader.readPlateText();
        System.out.println("Kennzeichen: " + plate);
    }

    public static void testPlateReader(Mat image) {
        PlateReader reader = new PlateReader(image);
        System.out.println(reader.readPlateText());
    }
}
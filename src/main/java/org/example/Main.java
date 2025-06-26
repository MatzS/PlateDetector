package org.example;

import org.example.dataObjects.ContourElement;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        testSingleImage(Imgcodecs.imread("Bilder/Cars/car03.jpg"));
        // testPlateReader(Imgcodecs.imread("Bilder/Kennzeichen/kennzeichen_3.jpg",
        // Imgcodecs.IMREAD_GRAYSCALE));
    }

    public static void testSingleImage(Mat image) {
        PlateDetector detector = new PlateDetector(image);
        Mat result = detector.detectPlate();
        Mat grey = new Mat();
        Imgproc.cvtColor(result, grey, Imgproc.COLOR_BGR2GRAY);
        PlateReader reader = new PlateReader(grey);

//        Imgcodecs.imwrite("Bilder/Konturen/kennzeichen.jpg", grey);

        String plate = reader.readPlateText();
        System.out.println("Kennzeichen: " + plate);
    }

    public static void testPlateReader(Mat image) {
        PlateReader reader = new PlateReader(image);
        System.out.println(reader.readPlateText());
    }
}
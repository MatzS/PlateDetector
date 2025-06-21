package org.example;

import org.example.dataObjects.ContourElement;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        testSingleImage(Imgcodecs.imread("Bilder/CarsNeu/car5.jpg"));
        // testAllCarPictures();
        testPlateReader(Imgcodecs.imread("Bilder/Kennzeichen/kennzeichen.jpg", Imgcodecs.IMREAD_GRAYSCALE));
    }

    public static void testSingleImage(Mat image) {
        PlateDetector detector = new PlateDetector(image);
        detector.detectPlate();
    }

    public static void testAllCarPictures() throws IOException {
        List<ContourElement> test = DetectionTester.testAllImages("Bilder/Testerg");
    }

    public static void testPlateReader(Mat image) {
        PlateReader reader = new PlateReader(image);
        System.out.println(reader.readPlateText());
    }
}
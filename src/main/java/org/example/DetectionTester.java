package org.example;

import org.example.dataObjects.ContourElement;
import org.example.dataObjects.ContoursResult;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetectionTester {

    public static List<ContourElement> testAllImages(String foldername) throws IOException {
        File imageFolder = new File("Bilder/Cars");
        List<ContourElement> contours = new ArrayList<>();
        int count = 0;
        for (File imgFile : Objects.requireNonNull(imageFolder.listFiles())) {
            if (!imgFile.getName().endsWith(".jpg")) continue;
            Mat image = Imgcodecs.imread(imgFile.getAbsolutePath());
            PlateDetector detector = new PlateDetector(image);
            ContourElement result = detector.detectPlate();
            Visualizer.drawContour(result, foldername + "/prediction_" + count + ".jpg");
            contours.add(result);
            count++;
        }
        return contours;
    }
}

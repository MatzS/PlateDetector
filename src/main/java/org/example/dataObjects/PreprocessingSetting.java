package org.example.dataObjects;

import org.opencv.core.Size;

public class PreprocessingSetting {
    public double cannyt1;
    public double cannyt2;
    public Size gausSize;

    public double gausSigmaX;

    public static double CANNYT1_LOWEST_RANGE = 0;
    public static double CANNYT1_HIGHEST_RANGE = 200;
    public static double CANNYT2_LOWEST_RANGE = 0;
    public static double CANNYT2_HIGHEST_RANGE = 200;
    public static int GAUS_LOWEST_SIZE = 3;
    public static int GAUS_HIGHEST_SIZE = 11;


    public PreprocessingSetting(double cannyt1, double cannyt2, Size gausSize, double gausSigmaX) {
        this.cannyt1 = cannyt1;
        this.cannyt2 = cannyt2;
        this.gausSize = gausSize;
        this.gausSigmaX = gausSigmaX;
    }

    public PreprocessingSetting(double cannyt1, double cannyt2, Size gausSize) {
        this.cannyt1 = cannyt1;
        this.cannyt2 = cannyt2;
        this.gausSize = gausSize;
        this.gausSigmaX = 0;
    }
}

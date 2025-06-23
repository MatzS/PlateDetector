package org.example;

import org.opencv.core.Size;

/**
 * Enthält die Parameter und Grenzwerte für die verschiedenen
 * Verarbeitungsschritte.
 */
public class DetectionSettings {

    // Preprocessing
    public static int PRE_CANNY1 = 120;
    public static int PRE_CANNY2 = 200;
    public static Size PRE_GAUS_SIZE = new Size(3, 3);

    // Filter
    public static int FILTER_MIN_SIZE = 2000;
    public static int FILTER_MAX_SIZE = 15000;

    // Hierarchy
    public static int HY_NUM_CHILD_LOW = 7;
    public static int HY_NUM_CHILD_HIGH = 10;
    public static double HY_CHILD_COUNT_SCORE = 6;

    // Histogram
    public static double HI_HIST_FAKTOR_H = 2.2;
    public static double HI_HIST_FAKTOR_S = 2.2;
    public static double HI_HIST_FAKTOR_V = 1.8;

    // Shape
    public static double SH_SIZE_HIGHEST = 2000;
    public static double SH_SIZE_MIDDLE = 1000;
    public static double SH_SIZE_LOWEST = 800;

    public static double SH_ASPECT_LOWER = 3.2;
    public static double SH_ASPECT_HIGHER = 4.8;
    public static double SH_ASPECT_FACTOR = 1.0;

    public static double SH_EXTENT = 0.9;
    public static double SH_EXTENT_FACTOR = 1.0;
}

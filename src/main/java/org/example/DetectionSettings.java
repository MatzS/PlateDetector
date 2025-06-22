package org.example;

import org.opencv.core.Size;

public class DetectionSettings {




    // Preprocessing
    public static int PRE_CANNY1 = 120; // STEP 10
    public static int PRE_CANNY1_LOWER = 40;
    public static int PRE_CANNY1_UPPER = 120;

    public static int PRE_CANNY2 = 200; // STEP 10
    public static int PRE_CANNY2_LOWER = 100;
    public static int PRE_CANNY2_UPPER = 220;
    public static Size PRE_GAUS_SIZE = new Size(3,3); // 3 - 9

    //FIlter

    public static int FILTER_MIN_SIZE = 2000;
    public static int FILTER_MAX_SIZE = 15000;


    // Hierarchy

    public static int HY_NUM_CHILD_LOW = 7;
    public static int HY_NUM_CHILD_LOW_LOW = 4;
    public static int HY_NUM_CHILD_LOW_HIGH = 8;

    public static int HY_NUM_CHILD_HIGH = 10;

    public static int HY_NUM_CHILD_HIGH_LOW = 9;
    public static int HY_NUM_CHILD_HIGH_HIGH = 20;

    public static double HY_CHILD_COUNT_SCORE_LOW = 1.0;
    public static double HY_CHILD_COUNT_SCORE_HIGH = 5.0;
    public static double HY_CHILD_COUNT_SCORE = 6;

    public static double HY_CHILD_SIZE_FAKTOR_LOW = 1.0;
    public static double HY_CHILD_SIZE_FAKTOR_HIGH = 3.0;
    public static double HY_CHILD_SIZE_FAKTOR = 1.0;


    // Histogram
    public static double HI_HIST_FAKTOR_H_LOW = 1.0;
    public static double HI_HIST_FAKTOR_H_HIGH = 3.0;
    public static double HI_HIST_FAKTOR_H = 2.2;

    public static double HI_HIST_FAKTOR_S_LOW = 1.0;
    public static double HI_HIST_FAKTOR_S_HIGH = 3.0;
    public static double HI_HIST_FAKTOR_S = 2.2;

    public static double HI_HIST_FAKTOR_V_LOW = 1.0;
    public static double HI_HIST_FAKTOR_V_HIGH = 3.0;
    public static double HI_HIST_FAKTOR_V = 1.8;

    // Shape

    public static double SH_ASPECT_LOWER = 3.2;
    public static double SH_ASPECT_LOWER_LOW = 2.5;
    public static double SH_ASPECT_LOWER_HIGH = 4.0;

    public static double SH_ASPECT_HIGHER = 4.8;
    public static double SH_ASPECT_HIGHER_LOW = 3.8;
    public static double SH_ASPECT_HIGHER_HIGH = 6.0;

    public static double SH_ASPECT_FACTOR_LOW = 1.0;
    public static double SH_ASPECT_FACTOR_HIGH = 3.0;
    public static double SH_ASPECT_FACTOR = 1.0;

    public static double SH_EXTENT_LOWER = 0.8;
    public static double SH_EXTENT_HIGHER = 0.95;
    public static double SH_EXTENT = 0.9;

    public static double SH_EXTENT_FACTOR_LOWER = 1.0;
    public static double SH_EXTENT_FACTOR_HIGHER = 5.0;
    public static double SH_EXTENT_FACTOR = 1.0;
}

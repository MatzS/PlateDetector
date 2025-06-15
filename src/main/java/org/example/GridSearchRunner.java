package org.example;

import org.example.dataObjects.ContourElement;

import java.util.*;
import java.nio.file.*;
import java.io.File;

public class GridSearchRunner {

    static class PreprocessingConfig {
        int canny1, canny2;
        int gaussKernelSize;

        @Override
        public String toString() {
            return "CANNY1=" + canny1 + ", CANNY2=" + canny2 + ", GAUSS=" + gaussKernelSize;
        }
    }

    public static void runPreprocessingSearch() throws Exception {
        List<PreprocessingConfig> allConfigs = new ArrayList<>();

        // Schrittweite definieren
        int step = 10;

        // Gauss-Werte (ungerade zwischen 3 und 9)
        int[] gaussSizes = {3, 5, 7, 9};

        for (int c1 = DetectionSettings.PRE_CANNY1_LOWER; c1 <= DetectionSettings.PRE_CANNY1_UPPER; c1 += step)
            for (int c2 = DetectionSettings.PRE_CANNY2_LOWER; c2 <= DetectionSettings.PRE_CANNY2_UPPER; c2 += step)
                for (int gauss : gaussSizes) {
                    PreprocessingConfig cfg = new PreprocessingConfig();
                    cfg.canny1 = c1;
                    cfg.canny2 = c2;
                    cfg.gaussKernelSize = gauss;
                    allConfigs.add(cfg);
                }

        int bestScore = -1;
        PreprocessingConfig best = null;
        int counter = 0;

        System.out.println("Anzahl Kombinationen: " + allConfigs.size());

        for (PreprocessingConfig cfg : allConfigs) {
            System.out.print("Testing #" + counter + ": " + cfg);

            applyPreSettings(cfg);
            List<ContourElement> hits = DetectionTester.testAllImages("!");

            if (hits.size() > bestScore) {
                bestScore = hits.size();
                best = cfg;
                System.out.println("  ✅ NEW BEST (" + bestScore + " Treffer): " + best);
            } else {
                System.out.println("  (" + hits.size() + " Treffer)");
            }

            counter++;
        }

        System.out.println("\n🏆 Beste Preprocessing-Kombi: " + best + " mit " + bestScore + " Treffern");
    }

    private static void applyPreSettings(PreprocessingConfig cfg) {
        DetectionSettings.PRE_CANNY1 = cfg.canny1;
        DetectionSettings.PRE_CANNY2 = cfg.canny2;
        DetectionSettings.PRE_GAUS_SIZE = new org.opencv.core.Size(cfg.gaussKernelSize, cfg.gaussKernelSize);
    }
}
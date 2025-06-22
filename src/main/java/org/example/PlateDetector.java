package org.example;

import org.example.dataObjects.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.Arrays;
import java.util.List;

public class PlateDetector {

    private Preprocesser pp;

    private ContourFinder cf;


    private HierarchyScorer has;

    private HistogramScorer his;

    private ShapeScorer ss;

    private ScoreEvaluator se;

    private ContoursFilter filter;
    public static Mat src;

    public static Mat hsv;


    public PlateDetector(Mat src) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);
        PlateDetector.src = src;
        PlateDetector.hsv = hsv;
        double[] test = hsv.get(0,0);
        this.pp = new Preprocesser();
        this.cf = new ContourFinder();
        this.has = new HierarchyScorer();
        this.his = new HistogramScorer("Bilder/Kennzeichen");
        this.ss = new ShapeScorer();
        this.se = new ScoreEvaluator();
        this.filter = new ContoursFilter();
    }

    public Mat detectPlate() {
        PreprocessingResult ppResult = pp.preprocessingImage();
        ContoursResult contResult = cf.getContours(ppResult);

        // Scoring
        ContoursResult afterHierarchie = has.computeHierarchyScores(contResult);
        ContoursResult filtered = filter.filterContoursByArea(afterHierarchie, DetectionSettings.FILTER_MIN_SIZE,
                DetectionSettings.FILTER_MAX_SIZE);
        ContoursResult afterHistogram = his.computeHistogramScores(filtered);
        ContoursResult afterShape = ss.computeShapeScores(afterHistogram);
        ContoursResult result = se.computeTotalScores(afterShape);
        ContourElement best = result.getBestContour();


        //Output
        List<ContourElement> top10 = result.getNBestContours(10);
        Visualizer.drawNContours(top10, "Bilder");
        Visualizer.drawMat(ppResult.edges, "Bilder/Kanten.jpg");
        Visualizer.drawMat(ppResult.grey, "Bilder/Grey.jpg");
        Visualizer.drawAllContours(result);
        ScoreEvaluator.checkAllZeroScores(result);

        Mat resultMat = getResultImageMat(best.contour);
        Visualizer.drawMat(resultMat, "Bilder/result_mat.jpg");
        return resultMat;
    }

    private Mat getResultImageMat(MatOfPoint contour) {
        // 1. Konvertiere Kontur zu MatOfPoint2f (für minAreaRect)
        MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());

        // 2. Ermittle das RotatedRect (kleinste umschließende Rotationsbox)
        RotatedRect rect = Imgproc.minAreaRect(contour2f);

        // 3. Hole die Ecken des RotatedRect
        Point[] rectPoints = new Point[4];
        rect.points(rectPoints);

        // 4. Bestimme die Breite und Höhe des Rechtecks
        int width = (int) rect.size.width;
        int height = (int) rect.size.height;

        // Korrigiere Breite/Höhe falls nötig (z. B. bei Rotation)
        if (width <= 0 || height <= 0) return null;

        // 5. Definiere Zielpunkte für die "entrotierte" Ansicht (z. B. oben-links, oben-rechts, ...)
        Point[] dstPoints = new Point[] {
                new Point(0, height - 1),
                new Point(0, 0),
                new Point(width - 1, 0),
                new Point(width - 1, height - 1)
        };

        // 6. Perspektivtransformation berechnen
        Mat srcMat = Converters.vector_Point2f_to_Mat(Arrays.asList(rectPoints));
        Mat dstMat = Converters.vector_Point2f_to_Mat(Arrays.asList(dstPoints));
        Mat transform = Imgproc.getPerspectiveTransform(srcMat, dstMat);

        // 7. Originalbild ausschneiden
        Mat warped = new Mat();
        Imgproc.warpPerspective(src, warped, transform, new Size(width, height));
        Mat resized = new Mat();
        Imgproc.resize(warped, resized, new Size(640,100));
        return resized;
    }
}

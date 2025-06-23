package org.example;

import org.example.dataObjects.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * Wertet die Konturen in einem Bild aus
 * um das Kennzeichen zu finden.
 */
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

    /**
     * Sucht das Kennzeichen im Bild
     * @return Mat-Element des Kennzeichens
     */
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


        //Output zum Debuggen
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

    /**
     * Transformiert die gefundene Kontur in eine rechteckige Matrix
     * der Größe 640x100
     * @param contour Die Kontur
     * @return 640x100 Mat
     */
    private Mat getResultImageMat(MatOfPoint contour) {
        // 1. Konvertiere Kontur zu MatOfPoint2f
        MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());

        // 2. Ermittle das RotatedRect
        RotatedRect rect = Imgproc.minAreaRect(contour2f);

        // 3. Extrahiere Punkte (4 Ecken)
        Point[] rectPoints = new Point[4];
        rect.points(rectPoints);

        // 4. Sortiere Punkte: top-left, top-right, bottom-right, bottom-left
        Point[] ordered = orderPoints(rectPoints);

        // 5. Berechne Breite und Höhe basierend auf den tatsächlichen Distanzen
        double widthA = distance(ordered[2], ordered[3]); // bottom
        double widthB = distance(ordered[1], ordered[0]); // top
        double maxWidth = Math.max(widthA, widthB);

        double heightA = distance(ordered[0], ordered[3]); // left
        double heightB = distance(ordered[1], ordered[2]); // right
        double maxHeight = Math.max(heightA, heightB);

        // Zielpunkte in exakt derselben Reihenfolge
        Point[] dstPoints = new Point[] {
                new Point(0, 0),                       // top-left
                new Point(maxWidth - 1, 0),            // top-right
                new Point(maxWidth - 1, maxHeight - 1),// bottom-right
                new Point(0, maxHeight - 1)            // bottom-left
        };

        // 6. Perspektivtransformation berechnen
        Mat srcMat = Converters.vector_Point2f_to_Mat(Arrays.asList(ordered));
        Mat dstMat = Converters.vector_Point2f_to_Mat(Arrays.asList(dstPoints));
        Mat transform = Imgproc.getPerspectiveTransform(srcMat, dstMat);

        // 7. Bild extrahieren
        Mat warped = new Mat();
        Imgproc.warpPerspective(src, warped, transform, new Size(maxWidth, maxHeight));

        // 8. Optional: Normalisieren auf feste Ausgabegröße (z. B. 640x100)
        Mat resized = new Mat();
        Imgproc.resize(warped, resized, new Size(640, 100));

        return resized;
    }

    /**
     * Sortiert ein Array von Punkten
     * @param pts Eckpunkte des Rect-Elements
     * @return sortiertes Punkte Array
     */
    private Point[] orderPoints(Point[] pts) {
        // Summe (x + y): top-left ist kleinste, bottom-right ist größte
        // Differenz (y - x): top-right ist kleinste, bottom-left ist größte
        Point[] ordered = new Point[4];

        Arrays.sort(pts, Comparator.comparingDouble(p -> p.x + p.y));
        ordered[0] = pts[0]; // top-left
        ordered[2] = pts[3]; // bottom-right

        Arrays.sort(pts, Comparator.comparingDouble(p -> p.y - p.x));
        ordered[1] = pts[0]; // top-right
        ordered[3] = pts[3]; // bottom-left

        return ordered;
    }

    /**
     * Berechnet die Distanz zwischen zwei Punkten
     * @param p1 Punkt1
     * @param p2 Punkt2
     * @return Distanz zwischen den Punkten
     */
    private double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }
}

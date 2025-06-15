package org.example;

import org.example.dataObjects.*;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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

    public ContourElement detectPlate() {
        PreprocessingResult ppResult = pp.preprocessingImage();
        ContoursResult contResult = cf.getContours(ppResult);

        //Filtern
        ContoursResult filtered = filter.filterContoursByArea(contResult, DetectionSettings.FILTER_MIN_SIZE);

        // Scoring
        ContoursResult afterHierarchie = has.computeHierarchyScores(contResult);
        ContoursResult afterHistogram = his.computeHistogramScores(afterHierarchie);
        ContoursResult afterShape = ss.computeShapeScores(afterHistogram);
        ContoursResult result = se.computeTotalScores(afterShape);
        ContourElement best = result.getBestContour();

        //Output
        List<ContourElement> top10 = result.getNBestContours(10);
        Visualizer.drawNContours(top10, "Bilder");
        Visualizer.drawMat(ppResult.edges, "Bilder/Kanten.jpg");
        Visualizer.drawMat(ppResult.grey, "Bilder/Grey.jpg");
        Visualizer.drawAllContours(filter.filterContoursByArea(contResult, DetectionSettings.FILTER_MIN_SIZE));
        ScoreEvaluator.checkAllZeroScores(result);


        return best;
    }
}

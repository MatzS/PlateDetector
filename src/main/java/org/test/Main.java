package Main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {
    private static String testBildPfad = "Bilder/Auto01.jpg"; // Hier das zu testende Bild eintragen
    private static int minRectWidth = 40; // Hier die ungefähre breite eines Zeichens auf dem Kennzeichen eitragen,
                                          // damit keine unrelevanten Zeichen überprüft werden(z.B. Zahlen auf der
                                          // TÜV-Plakette)
    private static String alphabetPfad = "Bilder/Kennzeichen_Alphabet.jpg"; // Hier den Pfad zum Kennzeichen Alphabet
                                                                            // eintragen
    private static String templatesPfad = "Bilder/Templates/Template"; // Hier Bilder/Templates durch den gewünschten
                                                                       // Ordner ersetzen
    private static String konturenPfad = "Bilder/Konturen/Kontur"; // Hier den Ordner eintragen, in den die auf dem
                                                                   // Kennzeichen erkannten Konturen gespeichert werden
                                                                   // sollen

    // Template vorbereiten

    private static boolean rectsSindNahe(Rect r1, Rect r2) {
        // Rechtecke horizontal nah und vertikal überlappend oder sehr nah
        boolean horizontalOverlap = Math.abs(r1.x - r2.x) < 10;
        boolean verticalProximity = Math.abs(r1.y - r2.y) < 40;
        return horizontalOverlap && verticalProximity;
    }

    private static Rect mergeRects(Rect r1, Rect r2) {
        int x1 = Math.min(r1.x, r2.x);
        int y1 = Math.min(r1.y, r2.y);
        int x2 = Math.max(r1.x + r1.width, r2.x + r2.width);
        int y2 = Math.max(r1.y + r1.height, r2.y + r2.height);
        return new Rect(x1, y1, x2 - x1, y2 - y1);
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Map<String, Mat> templates = new HashMap<>();

        Mat alphabet = Imgcodecs.imread(alphabetPfad, Imgcodecs.IMREAD_GRAYSCALE);
        Imgproc.threshold(alphabet, alphabet, 127, 255, Imgproc.THRESH_BINARY_INV);

        // Finde Konturen der einzelnen Buchstaben
        List<MatOfPoint> konturen = new ArrayList<>();
        Imgproc.findContours(alphabet.clone(), konturen, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // BoundingRects vorbereiten
        List<Rect> boundingRects = new ArrayList<>();
        for (MatOfPoint kontur : konturen) {
            boundingRects.add(Imgproc.boundingRect(kontur));
        }

        // Konturen für Umlaute mergen
        List<Rect> mergedRects = new ArrayList<>();
        for (Rect rect : boundingRects) {
            boolean istGemerged = false;
            for (int i = 0; i < mergedRects.size(); i++) {
                Rect existierendesRect = mergedRects.get(i);
                if (rectsSindNahe(existierendesRect, rect)) {
                    mergedRects.set(i, mergeRects(existierendesRect, rect));
                    istGemerged = true;
                    break;
                }
            }
            if (!istGemerged) {
                mergedRects.add(rect);
            }
        }

        // Entferne sehr kleine Rechtecke (vermutlich Punkte o. Artefakte)
        mergedRects.removeIf(r -> r.width * r.height < 150); // ggf. Schwelle anpassen

        // Gruppiere nach Zeilen
        double zeilenThreshold = 20.0; // Toleranz für gleiche Y-Position (ggf. anpassen)
        List<List<Rect>> zeilen = new ArrayList<>();

        mergedRects.sort(Comparator.comparingInt(r -> r.y));
        for (Rect rect : mergedRects) {
            boolean hinzugefügt = false;
            for (List<Rect> zeile : zeilen) {
                if (Math.abs(zeile.get(0).y - rect.y) < zeilenThreshold) {
                    zeile.add(rect);
                    hinzugefügt = true;
                    break;
                }
            }
            if (!hinzugefügt) {
                List<Rect> neuezeile = new ArrayList<>();
                neuezeile.add(rect);
                zeilen.add(neuezeile);
            }
        }

        // Innerhalb jeder Zeile nach X sortieren
        for (List<Rect> zeile : zeilen) {
            zeile.sort(Comparator.comparingInt(r -> r.x));
        }

        // Zeichen zuordnen
        String[] zeichenZeilen = { "ABCDEFGHIJKLM", "NOPQRSTUVWXYZ", "ÄÖÜ1234567890" };

        for (int i = 0; i < zeilen.size(); i++) {
            List<Rect> zeile = zeilen.get(i);
            String zeichen = zeichenZeilen[i];
            for (int j = 0; j < zeile.size() && j < zeichen.length(); j++) {
                Rect rect = zeile.get(j);
                Mat roi = new Mat(alphabet, rect);

                Imgcodecs.imwrite(templatesPfad + zeichen.charAt(j) + ".jpg", roi);

                templates.put(String.valueOf(zeichen.charAt(j)), roi);
            }
        }

        // Erkennung der Kennzeichen

        Mat auto = Imgcodecs.imread(testBildPfad, Imgcodecs.IMREAD_GRAYSCALE); // Hier die Testbilder eintragen
        Imgproc.threshold(auto, auto, 127, 255, Imgproc.THRESH_BINARY_INV);

        // Finde Buchstaben im Bild
        List<MatOfPoint> kennzeichenKonturen = new ArrayList<>();
        Imgproc.findContours(auto.clone(), kennzeichenKonturen, new Mat(), Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE);

        kennzeichenKonturen.sort(Comparator.comparing(cnt -> Imgproc.boundingRect(cnt).x));

        StringBuilder erkannterText = new StringBuilder();
        int konturZähler = 0;
        for (MatOfPoint kontur : kennzeichenKonturen) {
            Rect rect = Imgproc.boundingRect(kontur);
            double aspectRatio = (double) rect.width / rect.height;
            if (aspectRatio > 0.5 && aspectRatio < 0.7 && rect.width > minRectWidth) {
                konturZähler++;
                Mat roi = new Mat(auto, rect);

                Imgcodecs.imwrite(konturenPfad + konturZähler + ".jpg", roi);

                // Vergleiche mit allen Templates
                String besteÜbereinstimmung = "?";
                double besterScore = Double.MAX_VALUE;

                for (Map.Entry<String, Mat> eintrag : templates.entrySet()) {
                    Mat template = eintrag.getValue();
                    Mat resizedRoi = new Mat();
                    Imgproc.resize(roi, resizedRoi, template.size());

                    Mat ergebnis = new Mat();
                    Imgproc.matchTemplate(resizedRoi, template, ergebnis, Imgproc.TM_SQDIFF);
                    Core.MinMaxLocResult mmr = Core.minMaxLoc(ergebnis);
                    if (mmr.minVal < besterScore) {
                        besterScore = mmr.minVal;
                        besteÜbereinstimmung = eintrag.getKey();
                    }
                }
                erkannterText.append(besteÜbereinstimmung);
            }
        }
        System.out.println("Erkanntes Kennzeichen: " + erkannterText.toString());
    }
}

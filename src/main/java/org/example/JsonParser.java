package org.example;

import com.google.gson.Gson;
import org.opencv.core.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    static class Root {
        List<Shape> shapes;
    }

    static class Shape {
        String label;
        List<List<Double>> points;
    }

    public static List<Point> parseKennzeichenPolygon(String jsonFilePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        Gson gson = new Gson();
        Root root = gson.fromJson(content, Root.class);

        if (root.shapes == null) return null;

        for (Shape shape : root.shapes) {
            if ("Kennzeichen".equals(shape.label)) {
                List<Point> polygon = new ArrayList<>();
                for (List<Double> pt : shape.points) {
                    polygon.add(new Point(pt.get(0), pt.get(1)));
                }
                return polygon;
            }
        }
        return null;
    }
}

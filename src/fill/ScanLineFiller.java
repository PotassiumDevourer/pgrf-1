package fill;

import model.Edge;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.PolygonRasterizer;

import java.util.ArrayList;
import java.util.Arrays;

public class ScanLineFiller implements  Filler {
    private PolygonRasterizer polygonRasterizer;
    private LineRasterizer lineRasterizer;
    private Polygon polygon;
    private int color;


    public ScanLineFiller(PolygonRasterizer polygonRasterizer, LineRasterizer lineRasterizer, Polygon polygon, int color) {
        this.polygonRasterizer = polygonRasterizer;
        this.lineRasterizer = lineRasterizer;
        this.polygon = polygon;
        this.color = color;
    }

    @Override
    public void fill() {
        if (polygon.getCount() < 3)
            return;
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < polygon.getCount(); i++) {
            int a = i;
            int b = (i + 1) % polygon.getCount();
            Edge edge = new Edge(polygon.getPoint(a), polygon.getPoint(b));
            if(!edge.isHorizontal()) {
                edge.orientate();
                edges.add(edge);
            }

        }
        int ymin = polygon.getPoint(0).getY();
        int ymax = polygon.getPoint(0).getY();
        for(int i = 1; i < polygon.getCount(); i++) {
            int current = polygon.getPoint(i).getY();
            if(current > ymax)
                ymax = current;
            if(current < ymin)
                ymin = current;
        }

        for (int y = ymin; y <= ymax; y++) {
            ArrayList<Integer> intersections = new ArrayList<>();
            for (int i = 0; i < edges.size(); i++) {
                var current = edges.get(i);
                if(!current.isIntersection(y))
                    continue;
                int x = current.getIntersection(y);
                intersections.add(x);
            }
            intersections.sort((a, b ) -> a.compareTo(b));

            for (int i = 0; i < intersections.size(); i +=2) {
                lineRasterizer.rasterize(intersections.get(i), y, intersections.get(i + 1), y, color, color);
            }
        }
        polygonRasterizer.rasterize(polygon);


    }


}

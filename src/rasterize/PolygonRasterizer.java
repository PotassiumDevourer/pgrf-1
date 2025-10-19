package rasterize;

import model.Point2D;
import model.Polygon;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public void rasterize(Polygon polygon) {
        if (polygon.getCount() > 2) {
            for (int i = 0; i < polygon.getCount() - 1; i++) {
                lineRasterizer.rasterize(polygon.getPoint(i), polygon.getPoint(i + 1), polygon.getColor1(), polygon.getColor2());
            }
            lineRasterizer.rasterize(polygon.getPoint(0), polygon.getPoint(polygon.getCount() - 1), polygon.getColor1(), polygon.getColor2());
        }
    }

    public void preview(Polygon polygon, Point2D newestPoint) {
        if (polygon.getCount() > 0) {
            for (int i = 0; i < polygon.getCount() - 1; i++) {
                lineRasterizer.rasterize(polygon.getPoint(i), polygon.getPoint(i + 1), polygon.getColor1(), polygon.getColor2());
            }
            lineRasterizer.rasterize(polygon.getPoint(polygon.getCount() - 1), newestPoint, polygon.getColor1(), polygon.getColor2());
            lineRasterizer.rasterize(newestPoint, polygon.getPoint(0), polygon.getColor1(), polygon.getColor2());

        }
    }

    public PolygonRasterizer(LineRasterizer rasterizer) {
        this.lineRasterizer = rasterizer;
    }

    public void setLineRasterizer(LineRasterizer rasterizer) {
        this.lineRasterizer = rasterizer;
    }
}

package rasterize;

import model.Polygon;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public void rasterize(Polygon polygon) {
        if(polygon.getCount() > 2) {
            for(int i = 0; i < polygon.getCount() - 1; i++) {
                lineRasterizer.rasterize(polygon.getPoint(i), polygon.getPoint(i + 1));
            }
            lineRasterizer.rasterize(polygon.getPoint(0), polygon.getPoint(polygon.getCount() - 1));
        }
    }

    public PolygonRasterizer(LineRasterizer rasterizer) {
        this.lineRasterizer = rasterizer;
    }

    public void setLineRasterizer(LineRasterizer rasterizer) {
        this.lineRasterizer = rasterizer;
    }
}

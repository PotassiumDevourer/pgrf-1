package rasterize;


import model.Line;
import model.Point2D;
import raster.RasterBufferedImage;

public abstract class LineRasterizer {
    protected RasterBufferedImage raster;

    public LineRasterizer(RasterBufferedImage raster) {
        this.raster = raster;
    }

    public void rasterize(int x1, int y1, int x2, int y2, int color) {
        rasterize(x1, y1, x2, y2, color, color);
    }

    public void rasterize(int x1, int y1, int x2, int y2, int color1, int color2) {

    }

    public void rasterize(Point2D p1, Point2D p2, int color1, int color2) {
        rasterize(p1.getX(), p1.getY(), p2.getX(), p2.getY(), color1, color2);
    }

    public void rasterize(Line line) {
        rasterize(line.getStart(), line.getEnd(), line.getColor1(), line.getColor2());
    }
}

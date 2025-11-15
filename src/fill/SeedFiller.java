package fill;


import model.Point2D;
import raster.Raster;

import java.util.OptionalInt;
import java.util.Stack;

public class SeedFiller implements Filler {
    private int fillColor;
    private Raster raster;
    private OptionalInt backgroundColor;
    private int startX, startY;

    public SeedFiller(int fillColor, Raster raster, int startX, int startY) {
        this.fillColor = fillColor;
        this.raster = raster;
        this.startX = startX;
        this.startY = startY;

        this.backgroundColor = raster.getPixel(startX, startY);
    }
    @Override
    public void fill() {
        Stack<Point2D> points = new Stack<Point2D>();
        points.push(new Point2D(startX, startY));
        while (!points.empty()) {
            var p = points.pop();
            if(backgroundColor.isPresent() &&  raster.getPixel(p.getX(), p.getY()).getAsInt() == backgroundColor.getAsInt() && !raster.getPixel(p.getX(), p.getY()).isEmpty()) {
                raster.setPixel(p.getX(), p.getY(), fillColor);
                points.push(new Point2D(p.getX() + 1, p.getY()));
                points.push(new Point2D(p.getX() - 1, p.getY()));
                points.push(new Point2D(p.getX() , p.getY() + 1));
                points.push(new Point2D(p.getX() , p.getY() - 1));
            }
        }

    }


}

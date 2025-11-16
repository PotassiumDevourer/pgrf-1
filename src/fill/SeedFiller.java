package fill;


import model.Point2D;
import raster.Raster;

import java.awt.*;
import java.util.OptionalInt;
import java.util.Stack;

public class SeedFiller implements Filler {
    private int fillColor;
    private Raster raster;
    private OptionalInt backgroundColor;
    private int startX, startY;
    private OptionalInt edgeColor;

    public SeedFiller(int fillColor, Raster raster, int startX, int startY) {
        this.fillColor = fillColor;
        this.raster = raster;
        this.startX = startX;
        this.startY = startY;
        this.backgroundColor = raster.getPixel(startX, startY);
        this.edgeColor = OptionalInt.empty();
    }

    public SeedFiller(int fillColor, Raster raster, int startX, int startY, int edgeColor) {
        this.fillColor = fillColor;
        this.raster = raster;
        this.startX = startX;
        this.startY = startY;
        this.backgroundColor = OptionalInt.empty();
        this.edgeColor = OptionalInt.of(edgeColor);
    }
    @Override
    public void fill() {
        if(new Color(backgroundColor.getAsInt()).getRGB() == new Color(fillColor).getRGB())
            return;
        Stack<Point2D> points = new Stack<Point2D>();
        points.push(new Point2D(startX, startY));
        while (!points.empty()) {
            var p = points.pop();
            if(backgroundColor.isPresent() && raster.getPixel(p.getX(), p.getY()).isPresent()) {
                var pixel = new Color(raster.getPixel(p.getX(), p.getY()).getAsInt()).getRGB();
                var newFill = new Color(fillColor).getRGB();
                if(pixel == new Color(backgroundColor.getAsInt()).getRGB()) {
                    raster.setPixel(p.getX(), p.getY(), newFill);
                    points.push(new Point2D(p.getX() + 1, p.getY()));
                    points.push(new Point2D(p.getX() - 1, p.getY()));
                    points.push(new Point2D(p.getX() , p.getY() + 1));
                    points.push(new Point2D(p.getX() , p.getY() - 1));
                }
            }
        }
    }




}

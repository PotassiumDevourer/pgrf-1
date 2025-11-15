package model;

import java.util.ArrayList;

public class Rectangle extends Polygon {
    public Rectangle() {
        points = new ArrayList<>();
    }

    public Rectangle(Point2D p1, Point2D p2, Point2D p3, int color1, int color2 ) {
        points = calculateRectangle(p1, p2, p3);
        this.color1 = color1;
        this.color2 = color2;
    }

    private ArrayList<Point2D> calculateRectangle(Point2D p1, Point2D p2, Point2D p3) {
        Point2D base = new Point2D(p2.getX() - p1.getX(), p2.getY() - p1.getY());
        Point2D heightDirection = new Point2D(p3.getX() - p1.getX(), p3.getY() - p1.getY());
        Point2D perpendicular = new Point2D(- base.getY(), base.getX() );
        var perpendicularLength = Math.sqrt(Math.pow(perpendicular.getX(),2) + Math.pow(perpendicular.getY(), 2));
        double[] normalized = {perpendicular.getX() / perpendicularLength, perpendicular.getY() / perpendicularLength};
        var height = heightDirection.getX() * normalized[0] + heightDirection.getY() * normalized[1];
        Point2D heightVector = new Point2D( (int)Math.round(normalized[0] * height), (int)Math.round(normalized[1] * height));
        ArrayList<Point2D> output = new ArrayList<Point2D>();
        output.add(p1);
        output.add(p2);
        output.add(new Point2D(p2.getX() + heightVector.getX(), p2.getY() + heightVector.getY()));
        output.add(new Point2D(p1.getX() + heightVector.getX(), p1.getY() + heightVector.getY()));

        return output;
    }
}

package model;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point2D> points;

    public Polygon() {
        this.points = new ArrayList<Point2D>();
    }
    public Point2D getPoint(int index) {
        return points.get(index);
    }

    public void addPoint(Point2D point) {
        points.add(point);
    }

    public int getCount() {
        return points.size();
    }
}

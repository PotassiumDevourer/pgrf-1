package model;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point2D> points;

    public Polygon() {
        this.points = new ArrayList<Point2D>();
    }

    public Polygon(ArrayList<Point2D> points) {
        this.points = points;
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

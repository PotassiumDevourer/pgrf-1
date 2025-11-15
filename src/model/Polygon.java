package model;

import java.util.ArrayList;
import java.util.Optional;

public class Polygon {
    protected ArrayList<Point2D> points;
    protected int color1;
    protected int color2;



    public Polygon(int color1, int color2) {
        this.points = new ArrayList<Point2D>();
        this.color1 = color1;
        this.color2 = color2;
    }


    public Polygon() {
        this.points = new ArrayList<Point2D>();
        this.color1 = 0xFF0000;
        this.color2 = 0xFF0000;
    }

    public void removePoint(int index) {
        points.remove(index);
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

    public void setPoint(int index, Point2D point) {
        points.set(index, point);
    }

    public int getCount() {
        return points.size();
    }

    public int getColor1() {
        return color1;
    }

    public int getColor2() {
        return color2;
    }

    public ArrayList<Point2D> getPoints() {
        return points;
    }


    public void setColor1(int color1) {
        this.color1 = color1;
    }

    public void setColor2(int color2) {
        this.color2 = color2;
    }
}

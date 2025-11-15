package model;

import tools.ColorInterpolator;

import java.awt.*;

public class Edge {
    private Point2D start;
    private Point2D end;


    public Edge(Point2D start, Point2D end ){
        this.start = start;
        this.end = end;
    }

    public Edge(int x1, int y1, int x2, int y2) {
        this.start = new Point2D(x1, y1);
        this.end = new Point2D(x2, y2);

    }

    public boolean isHorizontal() {
        return start.getY() == end.getY();
    }

    public boolean isIntersection(int y) {
        return start.getY() <= y && y < end.getY();
    }

    public Point2D intersect(Edge other) {
        int x1 = start.getX(), x2 = end.getX(), x3 = other.getStart().getX(), x4 = other.getEnd().getX();
        int y1 = start.getY(), y2 = end.getY(), y3 = other.getStart().getY(), y4 = other.getEnd().getY();
        int dx1 = x1 - x2, dx2 = x3 - x4, dy1 = y1 - y2, dy2 = y3 - y4;
        int denominator = dx1*dy2 - dy1*dx2;
        int dotP1 = x1*y2 - y1*x2, dotP2 = x3*y4 - y3*x4;
        int x = (dotP1 *dx2 - dx1 * dotP2)/denominator;
        int y = (dotP1*dy2 - dy1 * dotP2 );
        return new Point2D(x, y);
    }

    public static Point2D intersect(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
       // int dx1 = x1 - x2, dx2 = x3 - x4, dy1 = y1 - y2, dy2 = y3 - y4;
        int denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        int x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4))/denominator;
        int y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4))/denominator;
        return new Point2D(x, y);
    }

    public int getIntersection(int y) {
        int x1 = start.getX();
        int y1 = start.getY();
        int x2 = end.getX();
        int y2 = end.getY();

        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
            int tmp2 = y1;
            y1 = y2;
            y2 = tmp2;


        }
        float dx = (float) (x2 - x1);
        float k = dx == 0 ? 1 : (y2 - y1) / dx;
        float q = y1 - k * x1;
        return Math.round((y - q) / k);

    }

    public void orientate() {
        if(start.getY() > end.getY()) {
            var tmp = start;
            start = end;
            end = tmp;
        }
    }

    public Point2D getStart(){
        return start;
    }

    public Point2D getEnd() {
        return end;
    }

    public void setStart(Point2D start) {
        this.start = start;
    }

    public void setEnd(Point2D end) {
        this.end = end;
    }
}

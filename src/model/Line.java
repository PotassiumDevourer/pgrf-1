package model;


public class Line {
    private Point2D start;
    private Point2D end;
    public Line(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
    }

    public Line(int x1, int y1, int x2, int y2) {
        this.start = new Point2D(x1, y1);
        this.end = new Point2D(x2, y2);
    }

    public Point2D getStart(){
        return start;
    }

    public Point2D getEnd() {
        return end;
    }
}

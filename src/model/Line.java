package model;


public class Line {
    private Point2D start;
    private Point2D end;
    private int color1;
    private int color2;

    public Line(Point2D start, Point2D end, int color1, int color2) {
        this.start = start;
        this.end = end;
        this.color1 = color1;
        this.color2 = color2;
    }

    public Line(int x1, int y1, int x2, int y2, int color1, int color2) {
        this.start = new Point2D(x1, y1);
        this.end = new Point2D(x2, y2);
        this.color1 = color1;
        this.color2 = color2;
    }

    public int getColor1() {return color1;}
    public int getColor2() {return color2;}


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

package clip;

import model.Edge;
import model.Point2D;
import model.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Clipper {

    public ArrayList<Point2D> clip(ArrayList<Point2D> clipperPoints, ArrayList<Point2D> pointsToClip) {
        double area = 0;
        for (int i = 0; i < clipperPoints.size(); i++) {
           int k = (i + 1) % clipperPoints.size();
           Point2D p1 = clipperPoints.get(i);
           Point2D p2 = clipperPoints.get(k);
           area += (p2.getX() - p1.getX()) * (p2.getY() + p1.getY());
        }
        if(area < 0) {
            Collections.reverse(clipperPoints);
        }
        for (int i = 0; i < clipperPoints.size(); i++) {
            int k = (i + 1) % clipperPoints.size();
            pointsToClip = clipAgainstEdge(pointsToClip, clipperPoints.get(i), clipperPoints.get(k));
        }
        return pointsToClip;
    }



    public ArrayList<Point2D> clipAgainstEdge(ArrayList<Point2D> pointsToClip, Point2D point1, Point2D point2) {
        ArrayList<Point2D> newPoints = new ArrayList<>();
        for (int i = 0; i < pointsToClip.size(); i++) {
            int k = (i + 1) % pointsToClip.size();
            Point2D p1 = pointsToClip.get(i), p2 = pointsToClip.get(k);
            int pos1 = (point2.getX() - point1.getX()) * (p1.getY() - point1.getY()) - (point2.getY() - point1.getY()) * (p1.getX() - point1.getX());
            int pos2 = (point2.getX() - point1.getX()) * (p2.getY() - point1.getY()) - (point2.getY() - point1.getY()) * (p2.getX() - point1.getX());
            if(pos1 < 0 && pos2 < 0) {
                newPoints.add(p2);
                continue;
            }
            if(pos1 >= 0 && pos2 < 0) {
                newPoints.add(Edge.intersect(point1.getX(), point1.getY(), point2.getX(), point2.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY()));
                newPoints.add(p2);
                continue;
            }
            if(pos1 < 0 && pos2 >= 0 ) {
                newPoints.add(Edge.intersect(point1.getX(), point1.getY(), point2.getX(), point2.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY()));
            }
        }
        return  newPoints;
    }


}

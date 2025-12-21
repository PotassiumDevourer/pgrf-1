package solid;

import java.util.ArrayList;
import model.Point2D;
import transforms.Point3D;

public class Cylinder extends Solid implements Segmentable{
    int currentSegments;
    public Cylinder(int segments) {

      setCurrentSegments(segments);
    }

    @Override
    public int getCurrentSegments() {
        return currentSegments;
    }

    @Override
    public void setCurrentSegments(int segments) {
        if(segments < 1) {
            return;
        }
        this.currentSegments = segments;
        createCylinder(segments);
    }

    private void createCylinder(int segments) {
        vb.clear();
        ib.clear();
        var circle = calculateCirclePoints(0,0,0.5,segments);
        circle.sort((a, b) -> {
            double angleA = Math.atan2(a.getY() - 0, a.getX() - 0);
            double angleB = Math.atan2(b.getY() - 0, b.getX() - 0);
            return Double.compare(angleB, angleA);
        });
        int i1 = 0;
        for (int i = 0; i < circle.size(); i++) {
            Point3D current = circle.get(i);
            vb.add(current);
            vb.add(new Point3D(current.getX(), current.getY(), -1));
            int k = (i1 + 2) % (circle.size() * 2);
            addIndices(0xFF0000,i1 + 1,k + 1);
            addIndices(0xFF0000,i1,k);
            addIndices(0xFF0000,i1,i1 + 1);
            i1 += 2;
        }
    }

    private ArrayList<Point3D> calculateCirclePoints(double centerX, double centerY,
                                                     double radius, double segments) {
        ArrayList<Point3D> points = new ArrayList<>();
        double TWO_PI = Math.PI * 2.0;
        for (int i = 0; i < segments; i++) {
            double theta = TWO_PI * i / segments;
            double x = centerX + radius * Math.cos(theta);
            double y = centerY + radius * Math.sin(theta);
            points.add(new Point3D(x, y, 1));
        }
        return points;
    }

}

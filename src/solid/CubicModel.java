package solid;

import enums.CubicType;
import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

public class CubicModel extends  Solid implements Segmentable {
    Point3D p1, p2, p3, p4;
    Cubic calculationMatrix;
    int currentSegments;

    public CubicModel( Point3D p1, Point3D p2, Point3D p3, Point3D p4, CubicType cubic, int segments) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        Mat4 currentCubic = new Mat4();
        switch (cubic) {
            case CubicType.Bezier:
                currentCubic = Cubic.BEZIER;
                break;
            case CubicType.Coons:
                currentCubic = Cubic.COONS;
                break;
            case CubicType.Ferguson:
                currentCubic = Cubic.FERGUSON;
                break;
        }
        calculationMatrix = new Cubic(currentCubic, p1,p2,p3,p4);
        setCurrentSegments(segments);

    }

    public int getCurrentSegments() {
        return currentSegments;
    }

    public void setCurrentSegments(int segments) {
        if(segments < 1) {
            return;
        }
        this.currentSegments = segments;
        vb.clear();
        ib.clear();
        for (int i = 0; i <= segments ; i++) {
            double t = (double) i / segments;
            vb.add(calculationMatrix.compute(t));
            if(i != segments) {
                addIndices(0xFF0000 ,i, i + 1);
            }
        }
    }
}

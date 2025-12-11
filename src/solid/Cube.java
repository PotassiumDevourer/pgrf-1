package solid;

import transforms.Point3D;

public class Cube extends Solid {
    public Cube() {
        //bottom left
        vb.add(new Point3D(-0.5, -0.5, -0.5));
        vb.add(new Point3D(0.5, -0.5, -0.5));
        addIndices(0xFF0000,0,1);
        vb.add(new Point3D(-0.5, 0.5, -0.5));
        vb.add(new Point3D(0.5, 0.5, -0.5));
        addIndices(0xFF0000,2,3);
        addIndices(0xFF0000,0, 2);
        addIndices(0xFF0000,1,3);

        vb.add(new Point3D(-0.5, -0.5, 0.5));
        vb.add(new Point3D(0.5, -0.5, 0.5));
        addIndices(0xFF0000, 4,5);
        vb.add(new Point3D(-0.5, 0.5, 0.5));
        vb.add(new Point3D(0.5, 0.5, 0.5));
        addIndices(0xFF0000, 6,7);
        addIndices(0xFF0000,4,6);
        addIndices(0xFF0000,5,7);

        addIndices(0xFF0000,0,4,1,5,2,6,3,7);
    }
}

package solid;

import transforms.Point3D;

public class AxisSet extends Solid {
    public AxisSet() {
        vb.add(new Point3D(0,0, 0));
        vb.add(new Point3D(1,0, 0));
        addIndices(0xFF0000, 0, 1);

        vb.add(new Point3D(0,0, 0));
        vb.add(new Point3D(0,1, 0));
        addIndices(0x00FF00, 2, 3);
        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0, 0, 1));
        addIndices(0x0000FF, 4, 5);
        

    }
}

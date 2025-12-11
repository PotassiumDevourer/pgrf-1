package solid;

import transforms.Point3D;

public class Arrow extends Solid {
    public Arrow() {
        vb.add(new Point3D(-0.5, 0, 0));
        vb.add(new Point3D(0.4, 0, 0));
        vb.add(new Point3D(0.4, -0.1, 0));
        vb.add(new Point3D(0.5, 0, 0));
        vb.add(new Point3D(0.4, 0.1, 0));

        addIndices(0xFF0000,0,1);
        addIndices(0xFF0000,2,3);
        addIndices(0xFF0000,3,4);
        addIndices(0xFF0000,4,2);

    }
}

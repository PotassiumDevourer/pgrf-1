package solid;

import enums.RotationAxis;
import transforms.Point3D;

import java.awt.*;

public class AxisSet extends Solid {
    private int[] colorSet = {0xFF0000, 0x00FF00, 0x0000FF};
    private RotationAxis selectedAxis = RotationAxis.Z;
    public AxisSet() {
        vb.add(new Point3D(0,0, 0));
        vb.add(new Point3D(1,0, 0));
        addIndices(colorSet[0], 0, 1);

        vb.add(new Point3D(0,0, 0));
        vb.add(new Point3D(0,1, 0));
        addIndices(colorSet[1], 2, 3);
        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0, 0, 1));
        addIndices(colorSet[2], 4, 5);
        setSelectedAxis(RotationAxis.Z);

    }

    public void hideSelectedAxis() {
        cb.clear();
        for (int i = 0; i < colorSet.length; i++) {
            cb.add(new Color(colorSet[i]));
        }
    }
    public void showSelectedAxis() {
        cb.set(selectedAxis.ordinal(), new Color(0xFFFF00));
    }

    public void setSelectedAxis(RotationAxis axis) {
        cb.set(selectedAxis.ordinal(), new Color(colorSet[selectedAxis.ordinal()]) );
        selectedAxis = axis;
    }
}

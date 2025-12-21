package solid;

import enums.RotationAxis;
import transforms.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Solid {
    protected List<Integer> ib = new ArrayList<>();
    protected List<Point3D> vb = new ArrayList<>();
    protected List<Color> cb = new ArrayList<>();
    protected Mat4 model = new Mat4Identity();
    protected double xTranslate = 0;
    protected double yTranslate = 0;
    protected double zTranslate = 0;
    
    protected boolean isSelected = false;
    protected double scaleX = 1.0;
    protected double scaleY = 1.0;
    protected double scaleZ = 1.0;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<Integer> getIb() {
        return ib;
    }

    public List<Point3D> getVb() {
        return vb;
    }

    public List<Color> getCb() {
        return cb;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public double getScaleZ() {
        return scaleZ;
    }

    public void setScale(double scaleX, double scaleY, double scaleZ) {
        setModel(getModel().mul(new Mat4Transl(-xTranslate, -yTranslate, -zTranslate)));
        model = getModel().mul(new Mat4Scale(1/this.scaleX, 1/this.scaleY, 1/this.scaleZ));
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        model = getModel().mul(new Mat4Scale(this.scaleX, this.scaleY,this.scaleZ));
        setModel(getModel().mul(new Mat4Transl(xTranslate, yTranslate, zTranslate)) );
    }

    public void move(double x, double y, double z) {
        setModel(getModel().mul(new Mat4Transl(x,y,z)) );
        xTranslate += x;
        zTranslate += z;
        yTranslate += y;
    }

    public void rotate(double deg, RotationAxis axis) {
        setModel(getModel().mul(new Mat4Transl(-xTranslate, -yTranslate, -zTranslate)));
        switch (axis) {
            case X:
                setModel(getModel().mul(new Mat4RotX(Math.toRadians(deg))) );
                break;
            case Y:
                setModel(getModel().mul(new Mat4RotY(Math.toRadians(deg))) );
                break;
            case Z:
                setModel(getModel().mul(new Mat4RotZ(Math.toRadians(deg))) );
                break;
        }
        setModel(getModel().mul(new Mat4Transl(xTranslate, yTranslate, zTranslate)) );
    }

    protected void addIndices(int color, Integer... indices) {
        ib.addAll(Arrays.asList(indices));
        for (int i = 0; i < (indices.length / 2); i++) {
            cb.add(new Color(color));
        }
    }

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }
}

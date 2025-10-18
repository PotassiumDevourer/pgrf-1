package model;

import controller.Mode;

public class PointLocation {
    private int shapeIndex, pointIndex;
    private Mode mode;

    public PointLocation(int shapeIndex, int pointIndex, Mode mode) {
        this.shapeIndex = shapeIndex;
        this.pointIndex = pointIndex;
        this.mode = mode;
    }

    public PointLocation() {
        this.shapeIndex = -1;
        this.pointIndex = -1;
        this.mode = Mode.None;
    }

    public int getShapeIndex() {
        return shapeIndex;
    }

    public void setShapeIndex(int shapeIndex) {
        this.shapeIndex = shapeIndex;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}

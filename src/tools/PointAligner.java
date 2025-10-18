package tools;

import model.Point2D;

public final class PointAligner {
    private PointAligner() {
        throw new RuntimeException();
    }

    public static int measureDistance(Point2D point1, Point2D point2) {
        double dx = Math.abs(point1.getX() - point2.getX());
        double dy = Math.abs(point1.getY() - point2.getY());
        return (int)(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public static Point2D align(int x1, int y1, int x2, int y2) {
        int dx = (x2 - x1);
        int dy = (y2 - y1);

        int[] possibleValues = {Math.abs(dx), Math.abs(dy), Math.abs(dx - dy), Math.abs(dx + dy)};
        int minIndex = 0;
        int min = possibleValues[minIndex];
        for(int i = 1; i < possibleValues.length;i++) {
            if(min > possibleValues[i]) {
                min = possibleValues[i];
                minIndex = i;

            }
        }
        switch (minIndex) {
            case 0:
                // Nejmensi je rozdil x, usecka je vlastne vertikalni
                return new Point2D(x1, y2);
            case 1:
                // nejmensi je rozdil y, usecka je vlastne rovna cara
                return new Point2D(x2, y1);
            case 2:
                // nejmensi je rozdil x a y, usecka ma nejbliz k 1. diagonale
                return new Point2D(x2, y1 + dx);
            case 3:
                // nejmensi je soucet x a y, usecka ma nejbliz k 2. diagonale
                return new Point2D(x2, y1 - dx);
            default:
                return new Point2D(x1, y2);
        }
    }
}

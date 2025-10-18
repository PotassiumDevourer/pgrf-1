package tools;

import java.awt.*;
import java.util.Random;

public final class ColorInterpolator {
    private ColorInterpolator() {
        throw new RuntimeException();
    }

    public static int interpolate(int a1, int a2, int current, float[] color1, float[] color2) {
        float[] newColors = new float[3];
        float random = (float)Math.random();
        for (int i = 0; i < 3; i++) {
            float t = (current - a1) / (float)(a2 - a1);
            newColors[i] = (1 - t)*color1[i] + t*color2[i];
        }

        return new Color(newColors[0], newColors[1], newColors[2]).getRGB();
    }
}

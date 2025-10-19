package rasterize;

import raster.RasterBufferedImage;
import tools.ColorInterpolator;

import java.awt.*;

public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(RasterBufferedImage raster) {
        super(raster);
    }

    /// Rasterizace vyuziva trivialni algoritmus
    @Override
    public void rasterize(int x1, int y1, int x2, int y2, int color1, int color2) {
        Color c1 = new Color(color1);
        Color c2 = new Color(color2);
        if (x1 == x2 && y1 == y2) {
            raster.setPixel(x1, y2, c1.getRGB());
            return;
        }
        float[] colorComps1 = c1.getColorComponents(null);
        float[] colorComps2 = c2.getColorComponents(null);
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
            int tmp2 = y1;
            y1 = y2;
            y2 = tmp2;
            float[] tmpColor = colorComps1;
            colorComps1 = colorComps2;
            colorComps2 = tmpColor;

        }
        float dx = (float) (x2 - x1);
        float k = dx == 0 ? 1 : (y2 - y1) / dx;
        float q = y1 - k * x1;

        if (Math.abs(k) < 1) {

            for (int j = x1; j <= x2; j++) {
                var resultingColor = ColorInterpolator.interpolate(x1, x2, j, colorComps1, colorComps2);
                raster.setPixel(j, Math.round(k * j + q), resultingColor);
            }
        } else {
            if (y1 > y2) {
                int tmp = x1;
                x1 = x2;
                x2 = tmp;
                int tmp2 = y1;
                y1 = y2;
                y2 = tmp2;
                float[] tmpColor = colorComps1;
                colorComps1 = colorComps2;
                colorComps2 = tmpColor;
            }
            dx = (float) (x2 - x1);
            k = dx == 0 ? 0 : (y2 - y1) / dx;
            q = y1 - k * x1;
            for (int j = y1; j <= y2; j++) {
                var resultingColor = ColorInterpolator.interpolate(y1, y2, j, colorComps1, colorComps2);
                raster.setPixel(k == 0 ? x1 : Math.round((j - q) / k), j, resultingColor);
            }
        }

    }
}

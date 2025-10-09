package rasterize;

import raster.RasterBufferedImage;

public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(RasterBufferedImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {

        if(x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
            int tmp2 = y1;
            y1 = y2;
            y2 = tmp2;
        }
        float dx = (float)(x2 - x1);
        float k = dx == 0 ? 1 : (y2-y1)/dx;
        float q = y1 - k*x1;
        if(Math.abs(k)<1) {

            for (int j = x1; j <= x2; j++) {
                raster.setPixel(j, Math.round(k*j + q), 0xFF0000);
            }
        }
        else {
            if(y1 > y2) {
                int tmp = x1;
                x1 = x2;
                x2 = tmp;
                int tmp2 = y1;
                y1 = y2;
                y2 = tmp2;
            }
            dx = (float)(x2 - x1);
            k = dx == 0 ? 0 : (y2-y1)/dx;
            q = y1 - k*x1;
            for (int j = y1; j <= y2; j++) {
                raster.setPixel(k == 0 ? x1 : Math.round((j- q)/k), j , 0xFF0000);
            }
        }

    }
}

package raster;

import java.awt.image.BufferedImage;

public class RasterBufferedImage implements Raster {
    private final BufferedImage image;
    public RasterBufferedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    @Override
    public void setPixel(int x, int y, int color) {
        if(x >= getWidth() || x < 0 || y < 0 || y >= getHeight())
            return;
        image.setRGB(x, y, color);
    }

    @Override
    public int getColor(int x, int y) {
        // TODO az dalsi uloha
        return 0;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void clear() {
       image.getGraphics().clearRect(0,0, image.getWidth(), image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }
}

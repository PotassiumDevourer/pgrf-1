package raster;

import java.awt.image.BufferedImage;
import java.util.OptionalInt;

public class RasterBufferedImage implements Raster {
    private final BufferedImage image;

    public RasterBufferedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        if (x >= getWidth() || x < 0 || y < 0 || y >= getHeight())
            return;
        image.setRGB(x, y, color);
    }

    @Override
    public OptionalInt getPixel(int x, int y) {
        if(x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()  )
            return OptionalInt.empty();
        return OptionalInt.of( image.getRGB(x, y));
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
        image.getGraphics().clearRect(0, 0, image.getWidth(), image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }
}

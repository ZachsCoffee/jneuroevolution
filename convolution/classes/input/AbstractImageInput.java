package input;

import convolution.MatrixReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class AbstractImageInput implements MatrixReader {

    protected final BufferedImage bufferedImage;
    protected final float[] hsb = new float[3];

    public AbstractImageInput(File image) {
        try {
            Objects.requireNonNull(image);
            bufferedImage = ImageIO.read(image);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read file: "+image, e);
        }
    }

    @Override
    public int getRowCount() {
        return bufferedImage.getHeight();
    }

    @Override
    public int getColumnCount() {
        return bufferedImage.getWidth();
    }

    protected float[] getHsb(int x, int y) {
        int rgb = bufferedImage.getRGB(x, y);

        Color.RGBtoHSB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, hsb);

        return hsb;
    }
}

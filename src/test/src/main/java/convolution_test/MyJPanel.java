package convolution_test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MyJPanel extends JPanel {
    private final BufferedImage buffer = new BufferedImage(1200, 1600, BufferedImage.TYPE_BYTE_GRAY);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(buffer, 0, 0, this);
    }

    public void paintPixel(int row, int column, int color) {

        buffer.setRGB(column, row, (color << 16) | (color << 8) | color);
    }
}

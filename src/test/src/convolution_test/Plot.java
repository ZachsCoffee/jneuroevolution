package convolution_test;

import javax.swing.*;
import java.awt.*;

public class Plot {
    public MyJPanel main;

    public void open() {
        JFrame frame = new JFrame("Cursor");
        frame.setContentPane(main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200, 1600));
        frame.pack();
        frame.setVisible(true);
    }
    {
        main = new MyJPanel();
        main.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    }
}

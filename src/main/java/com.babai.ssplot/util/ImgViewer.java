package util;

import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImgViewer {
    private JFrame frmMain;
    private JLabel lblView;

    public ImgViewer() {
        frmMain = new JFrame("Preview");
        lblView = new JLabel();
        lblView.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        lblView.setHorizontalAlignment(JLabel.CENTER);
        frmMain.add(lblView);
    }

    public void show(BufferedImage img) {
        frmMain.repaint();
        lblView.setIcon(new ImageIcon(img));
        frmMain.setVisible(true);
        frmMain.pack();
    }
}

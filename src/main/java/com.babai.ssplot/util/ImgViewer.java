package util;

import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImgViewer {
	private JFrame frmMain;
	private JLabel lblView;

	private ImgViewer() {
		frmMain = new JFrame("Preview");
		lblView = new JLabel();
		lblView.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		lblView.setHorizontalAlignment(JLabel.CENTER);
		frmMain.add(lblView);
	}

	private void showImpl(BufferedImage img) {
		frmMain.repaint();
		lblView.setIcon(new ImageIcon(img));
		frmMain.setVisible(true);
		frmMain.pack();
	}

	public static void show(BufferedImage img) {
		new ImgViewer().showImpl(img);
	}
}

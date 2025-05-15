/*
 * ImgViewer.java
 * 
 * Copyright 2024-2025 Subhraman Sarkar <suvrax@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

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

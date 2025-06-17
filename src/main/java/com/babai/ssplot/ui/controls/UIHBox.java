package com.babai.ssplot.ui.controls;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/** specialization of JPanel that acts like a horizontal box */
public class UIHBox extends JPanel {
	private FlowLayout layout;
	
	public UIHBox() {
		layout = new FlowLayout();
		setLayout(layout);
	}
	
	public UIHBox align(int align) {
		layout.setAlignment(align);
		setLayout(layout);
		return this;
	}
	
	public UIHBox gap(int hgap, int vgap) {
		layout.setHgap(hgap);
		layout.setVgap(vgap);
		setLayout(layout);
		return this;
	}
	
	public UIHBox border(Border b) {
		setBorder(b);
		return this;
	}
	
	public UIHBox emptyBorder(int gap) {
		return border(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
	}
}

package com.babai.ssplot.ui.controls;

import java.awt.FlowLayout;
import javax.swing.JPanel;

/** specialization of JPanel that acts like a horizontal box */
public class UIHBox extends JPanel implements UIBordered<UIHBox> {
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
}

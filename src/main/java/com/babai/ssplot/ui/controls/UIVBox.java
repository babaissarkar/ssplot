package com.babai.ssplot.ui.controls;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class UIVBox extends JPanel {
	private BoxLayout layout;
	
	public UIVBox() {
		layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
	}
	
	public UIVBox border(Border b) {
		setBorder(b);
		return this;
	}
	
	public UIVBox emptyBorder(int gap) {
		return border(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
	}
}

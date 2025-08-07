package com.babai.ssplot.ui.controls;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class UIVBox extends JPanel implements UIBordered<UIVBox> {
	private BoxLayout layout;
	
	public UIVBox() {
		layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
	}
}

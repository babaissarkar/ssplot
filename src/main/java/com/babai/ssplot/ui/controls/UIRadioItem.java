package com.babai.ssplot.ui.controls;

import javax.swing.JRadioButtonMenuItem;

public class UIRadioItem extends JRadioButtonMenuItem {
	public UIRadioItem text(String text) {
		setText(text);
		return this;
	}
	
	public UIRadioItem selected(boolean sel) {
		setSelected(sel);
		return this;
	}
	
	public UIRadioItem onClick(Runnable action) {
		addActionListener(e -> action.run());
		return this;
	}
}

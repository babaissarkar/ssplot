package com.babai.ssplot.ui.controls;

import javax.swing.JCheckBoxMenuItem;

public class UICheckItem extends JCheckBoxMenuItem {
	public UICheckItem text(String text) {
		setText(text);
		return this;
	}
	
	public UICheckItem selected(boolean sel) {
		setSelected(sel);
		return this;
	}
	
	public UICheckItem onClick(Runnable action) {
		addActionListener(e -> action.run());
		return this;
	}
}

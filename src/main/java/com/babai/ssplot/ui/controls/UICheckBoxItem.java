package com.babai.ssplot.ui.controls;

import javax.swing.JCheckBoxMenuItem;

public class UICheckBoxItem extends JCheckBoxMenuItem {
	
	public UICheckBoxItem selected(boolean sel) {
		setSelected(sel);
		return this;
	}
	
	public UICheckBoxItem onClick(Runnable action) {
		addActionListener(e -> action.run());
		return this;
	}
}

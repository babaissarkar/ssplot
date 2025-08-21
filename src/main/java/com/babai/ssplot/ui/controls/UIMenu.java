package com.babai.ssplot.ui.controls;

import java.awt.Component;

import javax.swing.JMenu;

public class UIMenu extends JMenu {
	
	public UIMenu text(String text) {
		setText(text);
		return this;
	}
	
	public UIMenu content(Component... children) {
		for (var child : children) {
			this.add(child);
		}
		return this;
	}
}

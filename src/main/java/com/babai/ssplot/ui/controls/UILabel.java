package com.babai.ssplot.ui.controls;

import java.awt.Component;

import javax.swing.JLabel;

public class UILabel extends JLabel {
	
	public UILabel() {
		// for now, labels are left aligned inside layout manager by default
		setAlignmentX(Component.LEFT_ALIGNMENT);
	}
	
	public UILabel text(String text) {
		setText(text);
		return this;
	}
	
	public UILabel bind(StateVar<String> text) {
		setText(text.get());
		text.onChange(() -> setText(text.get()));
		return this;
	}
}

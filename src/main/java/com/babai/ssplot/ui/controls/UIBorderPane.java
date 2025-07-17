package com.babai.ssplot.ui.controls;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

public class UIBorderPane extends JPanel {
	private BorderLayout layout;
	
	public UIBorderPane() {
		layout = new BorderLayout();
		setLayout(layout);
	}
	
	public UIBorderPane north(Component c) {
		add(c, BorderLayout.NORTH);
		return this;
	}
	
	public UIBorderPane south(Component c) {
		add(c, BorderLayout.SOUTH);
		return this;
	}
	
	public UIBorderPane east(Component c) {
		add(c, BorderLayout.EAST);
		return this;
	}
	
	public UIBorderPane west(Component c) {
		add(c, BorderLayout.WEST);
		return this;
	}
	
	public UIBorderPane center(Component c) {
		add(c, BorderLayout.CENTER);
		return this;
	}

}

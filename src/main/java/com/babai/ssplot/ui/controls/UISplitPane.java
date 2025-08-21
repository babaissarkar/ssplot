package com.babai.ssplot.ui.controls;

import java.awt.Component;

import javax.swing.JSplitPane;

public class UISplitPane extends JSplitPane {
	
	public UISplitPane type(int orientation) {
		setOrientation(orientation);
		return this;
	}
	
	public UISplitPane dividerLoc(int loc) {
		setDividerLocation(loc);
		return this;
	}
	
	public UISplitPane top(Component c) {
		setTopComponent(c);
		return this;
	}
	
	public UISplitPane bottom(Component c) {
		setBottomComponent(c);
		return this;
	}
}

package com.babai.ssplot.ui.controls;

import javax.swing.JComponent;
import javax.swing.JToolBar;

public class DUI {
	
	public static UIButton button() {
		return new UIButton();
	}
	
	public static UIHBox hbox(JComponent... children) {
		var hbox = new UIHBox();
		for (var child : children) {
			hbox.add(child);
		}
		return hbox;
	}
	
	public static UIVBox vbox(JComponent... children) {
		var vbox = new UIVBox();
		for (var child : children) {
			vbox.add(child);
		}
		return vbox;
	}
	
	public static JToolBar toolbar(JComponent... children) {
		var toolbar = new JToolBar();
		for (var child : children) {
			toolbar.add(child);
		}
		return toolbar;
	}
}

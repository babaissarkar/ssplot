package com.babai.ssplot.ui.controls;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class UIMenuItem extends JMenuItem {
	
	public UIMenuItem text(String text) {
		setText(text);
		return this;
	}
	
	public UIMenuItem hotkey(String shortcut) {
		this.setAccelerator(KeyStroke.getKeyStroke(shortcut));
		return this;
	}
	
	public UIMenuItem onClick(Runnable action) {
		this.addActionListener(e -> action.run());
		return this;
	}
}

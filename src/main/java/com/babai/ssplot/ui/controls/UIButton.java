package com.babai.ssplot.ui.controls;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class UIButton extends JButton {

	public UIButton icon(String resource) {
		setIcon(new ImageIcon(getClass().getResource(resource)));
		return this;
	}
	
	public UIButton tooltip(String tip) {
		setToolTipText(tip);
		return this;
	}
	
	public UIButton enabled(boolean enable) {
		setEnabled(enable);
		return this;
	}
	
	public UIButton onClick(Runnable action) {
		addActionListener(e -> action.run());
		return this;
	}
}

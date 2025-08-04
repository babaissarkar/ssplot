package com.babai.ssplot.ui.controls;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class UIButton extends JButton {

	public UIButton icon(String resource) {
		setIcon(new ImageIcon(getClass().getResource(resource)));
		return this;
	}
	
	public UIButton text(String text) {
		setText(text);
		return this;
	}
	
	public UIButton tooltip(String tip) {
		setToolTipText(tip);
		return this;
	}
	
	// statevar change -> enabled property change
	public UIButton enabled(StateVar<Boolean> enabled) {
		setEnabled(enabled.get());
		enabled.onChange(() -> setEnabled(enabled.get()));
		return this;
	}
	
	public UIButton enabled(boolean enabled) {
		setEnabled(enabled);
		return this;
	}
	
	public UIButton onClick(Runnable action) {
		addActionListener(e -> action.run());
		return this;
	}
}

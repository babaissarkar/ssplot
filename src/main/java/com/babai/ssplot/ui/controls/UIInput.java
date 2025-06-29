package com.babai.ssplot.ui.controls;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class UIInput extends JTextField {
	private static final Font monoFont =
			new Font("monospace", Font.PLAIN, 14);
	
	public UIInput() {
		setFont(monoFont);
		setHorizontalAlignment(JTextField.CENTER);
	}
	
	public UIInput columns(int columns) {
		setColumns(columns);
		return this;
	}
	
	public UIInput text(String text) {
		setText(text);
		return this;
	}
	
	// statevar change -> enabled property change
	public UIInput enabled(StateVar<Boolean> enabled) {
		setEnabled(enabled.get());
		enabled.bind(() -> setEnabled(enabled.get()));
		return this;
	}
	
	public UIInput enabled(boolean enabled) {
		setEnabled(enabled);
		return this;
	}
	
	public UIInput numeric(boolean numeric) {
		var keyListener = new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!Character.isDigit(c) && c != '-' &&
						!Character.isISOControl(c)) {
					if ( (c == '.' && getText().contains(".")) || c != '.' ) {
						e.consume();
					}
				}
			}
		};
		
		if (numeric) {
			addKeyListener(keyListener);
		} else {
			removeKeyListener(keyListener);
		}
		
		return this;
	}
	
	public Double value() {
		return Double.parseDouble(getText());
	}
	
	public boolean empty() {
		return getText().isEmpty();
	}
}

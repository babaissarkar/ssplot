package com.babai.ssplot.ui;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class CenteredField extends JTextField {
	private static final Font monoFont =
		new Font("monospace", Font.PLAIN, 14);
	
	public CenteredField(int count) {
		this("", count);
	}
	
	public CenteredField(String text, int count) {
		super(text, count);
		setFont(monoFont);
		setHorizontalAlignment(JTextField.CENTER);
	}
	
	public void setNumeric(boolean numeric) {
		var keyListener = new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!Character.isDigit(c) && c != '.' && c != '-' &&
						!Character.isISOControl(c)) {
					e.consume();
				}
			}
		};
		
		if (numeric) {
			addKeyListener(keyListener);
		} else {
			removeKeyListener(keyListener);
		}
	}
}

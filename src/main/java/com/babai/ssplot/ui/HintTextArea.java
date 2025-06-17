package com.babai.ssplot.ui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

/** JTextArea with hint text that gets cleared as soon as user starts typing */
public class HintTextArea extends JTextArea {	
	public void setHintText(String hint) {
		if (!hint.isEmpty()) {
			setForeground(Color.GRAY);
			setText(hint);
			addKeyListener(new KeyAdapter() {
				private boolean cleared = false;
	
				@Override
				public void keyPressed(KeyEvent e) {
					// FIXME: this is triggered when hint is showing
					// and user press non-char key, such as backspace,
					// causing a glitch effect.
					if (!cleared) {
						setText("");
						setForeground(Color.BLACK);
						cleared = true;
					}
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					if (cleared && getText().isEmpty()) {
						setText(hint);
						setForeground(Color.GRAY);
						setCaretPosition(0);
						cleared = false;
					}
				}
			});
		}
	}
}

/*
 * HintTextArea.java
 * 
 * Copyright 2025 Subhraman Sarkar <suvrax@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

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

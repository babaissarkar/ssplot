/*
 * CenteredField.java
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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import com.babai.ssplot.ui.controls.DUI.Text;

public class CenteredField extends JTextField {	
	public CenteredField(int count) {
		this("", count);
	}
	
	public CenteredField(String text, int count) {
		super(text, count);
		setFont(Text.monoFont);
		setHorizontalAlignment(JTextField.CENTER);
	}
	
	public void setNumeric(boolean numeric) {
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
	}
}

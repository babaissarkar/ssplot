/*
 * UIInput.java
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

package com.babai.ssplot.ui.controls;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.babai.ssplot.ui.controls.DUI.Text;

public class UIInput extends JTextField {	
	public UIInput() {
		setFont(Text.monoFont);
		setMargin(new Insets(5, 5, 5, 5));
		setHorizontalAlignment(JTextField.CENTER);
	}
	
	public UIInput chars(int columns) {
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
		enabled.onChange(() -> setEnabled(enabled.get()));
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
	
	public Integer intValue() {
		return Integer.parseInt(getText());
	}
	
	public boolean empty() {
		return getText().isEmpty();
	}
	
	public UIInput onChange(Runnable action) {
		getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				action.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				action.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				action.run();
			}
		});
		return this;
	}
	
	public UIInput onChange(Consumer<String> textAction) {
		getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				textAction.accept(getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				textAction.accept(getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				textAction.accept(getText());
			}
		});
		return this;
	}
}

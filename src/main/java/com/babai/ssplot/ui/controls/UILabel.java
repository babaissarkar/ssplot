/*
 * UILabel.java
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

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;

public class UILabel extends JLabel {
	
	public UILabel() {
		// for now, labels are left aligned inside layout manager by default
		setAlignmentX(Component.LEFT_ALIGNMENT);
	}
	
	public UILabel text(String text) {
		setText(text);
		return this;
	}
	
	// Binding type: text StateVar -> this class
	public UILabel bindToUI(StateVar<String> text) {
		setText(text.get());
		text.onChange(() -> setText(text.get()));
		return this;
	}

	public UILabel font(Font font) {
		setFont(font);
		return this;
	}
}

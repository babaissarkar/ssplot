/*
 * UIButton.java
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

import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class UIButton extends JButton implements UIStylizable<UIButton> {

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
	
	public UIButton margin(int top, int left, int bottom, int right) {
		setMargin(new Insets(top, left, bottom, right));
		return this;
	}
	
	public UIButton margin(int gap) {
		return margin(gap, gap, gap, gap);
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

/*
 * UIBorderPane.java
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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

public class UIBorderPane extends JPanel implements UIStylizable<UIBorderPane> {
	private BorderLayout layout;
	
	public UIBorderPane() {
		layout = new BorderLayout();
		setLayout(layout);
	}
	
	public UIBorderPane north(Component c) {
		add(c, BorderLayout.NORTH);
		return this;
	}
	
	public UIBorderPane south(Component c) {
		add(c, BorderLayout.SOUTH);
		return this;
	}
	
	public UIBorderPane east(Component c) {
		add(c, BorderLayout.EAST);
		return this;
	}
	
	public UIBorderPane west(Component c) {
		add(c, BorderLayout.WEST);
		return this;
	}
	
	public UIBorderPane center(Component c) {
		add(c, BorderLayout.CENTER);
		return this;
	}

}

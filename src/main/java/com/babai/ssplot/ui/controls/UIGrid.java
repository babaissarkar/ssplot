/*
 * UIGrid.java
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

public class UIGrid extends JPanel implements UIStylizable<UIGrid> {
	private GridBagConstraints gbc;
	
	public UIGrid() {
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		setLayout(new GridBagLayout());
	}
	
	public UIGrid anchor(int anchor) {
		gbc.anchor = anchor;
		return this;
	}
	
	public UIGrid fill(int fill) {
		gbc.fill = fill;
		return this;
	}
	
	public UIGrid insets(Insets insets) {
		gbc.insets = insets;
		return this;
	}
	
	public UIGrid insets(int gap) {
		gbc.insets = new Insets(gap, gap, gap, gap);
		return this;
	}
	
	public UIGrid weightx(double weightx) {
		gbc.weightx = weightx;
		return this;
	}
	
	public UIGrid weighty(double weighty) {
		gbc.weighty = weighty;
		return this;
	}
	
	public UIGrid spanx(int span) {
		gbc.gridwidth = span;
		return this;
	}
	
	public UIGrid spany(int span) {
		gbc.gridheight = span;
		return this;
	}
	
	public UIGrid row() {
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy++;
		return this;
	}
	
	public UIGrid column(Component comp) {
		add(comp, gbc);
		gbc.gridx++;
		return this;
	}
}

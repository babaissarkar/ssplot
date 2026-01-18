/*
 * UICheckBoxItem.java
 *
 * Copyright 2025-2026 Subhraman Sarkar <suvrax@gmail.com>
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
 */

package com.babai.ssplot.ui.controls;

import javax.swing.JCheckBoxMenuItem;

public class UICheckBoxItem extends JCheckBoxMenuItem {
	
	public UICheckBoxItem selected(boolean sel) {
		setSelected(sel);
		return this;
	}
	
	public UICheckBoxItem onClick(Runnable action) {
		addActionListener(e -> action.run());
		return this;
	}
}

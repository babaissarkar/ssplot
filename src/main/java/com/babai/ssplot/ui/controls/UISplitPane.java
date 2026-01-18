/*
 * UISplitPane.java
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

import java.awt.Component;

import javax.swing.JSplitPane;

public class UISplitPane extends JSplitPane {
	
	public UISplitPane type(int orientation) {
		setOrientation(orientation);
		return this;
	}
	
	public UISplitPane dividerLoc(int loc) {
		setDividerLocation(loc);
		return this;
	}
	
	public UISplitPane top(Component c) {
		setTopComponent(c);
		return this;
	}
	
	public UISplitPane bottom(Component c) {
		setBottomComponent(c);
		return this;
	}
}

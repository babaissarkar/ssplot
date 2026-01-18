/*
 * UITabPane.java
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
import java.util.function.Consumer;

import javax.swing.JTabbedPane;

public class UITabPane extends JTabbedPane {
	public UITabPane tab(String tabName, Component c) {
		addTab(tabName, c);
		return this;
	}
	
	public UITabPane onChange(Consumer<Integer> action) {
		addChangeListener(e -> action.accept(getSelectedIndex()));
		return this;
	}
}

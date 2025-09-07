/*
 * UIFrame.java
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
import java.awt.LayoutManager;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import com.babai.ssplot.ui.CrashFrame;

import static com.babai.ssplot.ui.controls.DUI.borderPane;

public class UIFrame extends JInternalFrame implements UIStylizable<UIFrame> {

	public UIFrame() {
		super();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	public UIFrame title(String title) {
		setTitle(title);
		return this;
	}

	public UIFrame size(int width, int height) {
		setSize(width, height);
		return this;
	}

	public UIFrame location(int x, int y) {
		setLocation(x, y);
		return this;
	}
	
	public UIFrame closeOperation(int operation) {
		setDefaultCloseOperation(operation);
		return this;
	}

	public UIFrame visible(boolean visible) {
		setVisible(visible);
		return this;
	}

	public UIFrame resizable(boolean resizable) {
		setResizable(resizable);
		return this;
	}

	public UIFrame closable(boolean closable) {
		setClosable(closable);
		return this;
	}

	public UIFrame maximizable(boolean maximizable) {
		setMaximizable(maximizable);
		return this;
	}

	public UIFrame iconifiable(boolean iconifiable) {
		setIconifiable(iconifiable);
		return this;
	}

	public UIFrame content(Component component) {
		setContentPane(borderPane().center(component));
		return this;
	}

	public UIFrame layout(LayoutManager layout) {
		getContentPane().setLayout(layout);
		return this;
	}

	public UIFrame packFrame() {
		pack();
		return this;
	}

	public UIFrame moveToFrontLater() {
		SwingUtilities.invokeLater(() -> {
			try {
				setSelected(true);
				moveToFront();
			} catch (Exception e) {
				//FIXME do we really want to do this in DUI code?
				CrashFrame.showCrash(e);
			}
		});
		return this;
	}
}

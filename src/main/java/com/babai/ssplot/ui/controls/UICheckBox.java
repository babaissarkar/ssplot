/*
 * UICheckBox.java
 *
 * Copyright 2026 Subhraman Sarkar <suvrax@gmail.com>
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

import javax.swing.JCheckBox;

public class UICheckBox extends JCheckBox
	implements UIStylizable<UICheckBox>, UIStateful<UICheckBox>
{
	public UICheckBox text(String text) {
		setText(text);
		return this;
	}
	
	public UICheckBox selected(boolean sel) {
		setSelected(sel);
		return this;
	}
	
	public UICheckBox onClick(Runnable action) {
		addActionListener(e -> action.run());
		return this;
	}
	
	// Binding type: selection StateVar -> JCheckBox's selection (on/off)
	public UICheckBox bindSelectionFrom(StateVar<Boolean> selection) {
		setSelected(selection.get());
		selection.onChange(selected -> setSelected(selected));
		return this;
	}
	
	// Binding type: selection StateVar <- JCheckBox's selection (on/off)
	public UICheckBox bindSelectionTo(StateVar<Boolean> selection) {
		selection.set(this.isSelected());
		this.onClick(() -> selection.set(this.isSelected()));
		return this;
	}
}


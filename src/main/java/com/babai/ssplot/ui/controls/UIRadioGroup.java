/*
 * UIRadioGroup.java
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
 * 
 * 
 */

package com.babai.ssplot.ui.controls;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class UIRadioGroup<E> extends UIHBox {
	private StateVar<E> selected = null;
	private ButtonGroup group = new ButtonGroup();
	
	public UIRadioGroup<E> options(E[] values, E selected) {
		if (values == null) return this;
		
		this.selected = new StateVar<>(selected);
		
		for (int i = 0; i < values.length; i++) {
			var radio = new JRadioButton(values[i].toString());
			group.add(radio);
			add(radio);
			
			if (values[i] == selected) {
				this.selected.set(selected);
				radio.setSelected(true);
			}
			
			final int idx = i;
			radio.addActionListener(e -> {
				if (radio.isSelected()) {
					this.selected.set(values[idx]);
				}
			});
		}
		
		// no match found or null passed as selected
		if (selected == null) {
			this.selected.set(values[0]);
		}
		return this;
	}
	
	// Binding type: selection StateVar <- RadioGroup's selection 
	public UIRadioGroup<E> bindSelectionTo(StateVar<E> selection) {
		if (selection != null) {
			this.selected = selection;
		}
		return this;
	}
}

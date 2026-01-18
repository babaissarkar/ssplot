/*
 * UIStateful.java
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

import javax.swing.JComponent;

@SuppressWarnings("unchecked")
public interface UIStateful<T extends JComponent> {
	
	// statevar change -> enabled property change
	default T enabled(StateVar<Boolean> enabled) {
		((JComponent) this).setEnabled(enabled.get());
		enabled.onChange(e -> ((JComponent) this).setEnabled(e));
		return (T) this;
	}

	default T enabled(boolean enabled) {
		((JComponent) this).setEnabled(enabled);
		return (T) this;
	}

	// statevar change -> visible property change
	default T visible(StateVar<Boolean> visible) {
		((JComponent) this).setVisible(visible.get());
		visible.onChange(v -> ((JComponent) this).setVisible(v));
		return (T) this;
	}

	default T visible(boolean visible) {
		((JComponent) this).setVisible(visible);
		return (T) this;
	}
}

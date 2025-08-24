/*
 * UIStylizable.java
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

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * A trait-style interface that adds fluent border-setting methods to Swing components.
 * <p>
 * This interface is designed to be used as a mixin for classes that already extend
 * {@link JComponent}. It provides default methods like {@code border(...)} and 
 * {@code emptyBorder(...)} that operate directly on {@code this}, enabling a
 * fluent API for UI configuration.
 * </p>
 *
 * <p>
 * To use this trait, a class should both extend a {@code JComponent} subclass
 * and implement {@code UIBordered}, like so:
 * </p>
 *
 * <pre>{@code
 * public class MyPanel extends JPanel implements UIBordered<MyPanel> {
 *     public MyPanel() {
 *         emptyBorder(10).setBackground(Color.LIGHT_GRAY);
 *     }
 * }
 * }</pre>
 *
 * <p>
 * This approach simulates trait or mixin behavior in Java, allowing you to
 * compose UI behaviors in a reusable and expressive way.
 * </p>
 *
 * @param <T> the concrete type of the component extending {@link JComponent}
 */
public interface UIStylizable<T extends JComponent> {
	
	@SuppressWarnings("unchecked")
	default T bg(Color c) {
		((JComponent) this).setBackground(c);
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	default T fg(Color c) {
		((JComponent) this).setForeground(c);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	default T border(Border b) {
		((JComponent) this).setBorder(b);
		return (T) this;
	}

	default T emptyBorder(int gap) {
		return border(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
	}
	
	default T emptyBorder(int top, int left, int bottom, int right) {
		return border(BorderFactory.createEmptyBorder(top, left, bottom, right));
	}
	
	@SuppressWarnings("unchecked")
	default TitledBorderBuilder<T> titledBorder() {
		return new TitledBorderBuilder<>((T) this);
	}

	/**
	 * A fluent builder for configuring a titled border and applying it to a component.
	 */
	class TitledBorderBuilder<T extends JComponent> {
		private final T component;
		private Border border = BorderFactory.createLineBorder(Color.GRAY);
		private String title = "";
		private int justification = TitledBorder.DEFAULT_JUSTIFICATION;
		private int position = TitledBorder.DEFAULT_POSITION;
		private Font font = null;
		private Color color = null;

		public TitledBorderBuilder(T component) {
			this.component = component;
		}

		public TitledBorderBuilder<T> lineBorder(Color c, int thickness) {
			this.border = BorderFactory.createLineBorder(c, thickness);
			return this;
		}

		public TitledBorderBuilder<T> title(String title) {
			this.title = title;
			return this;
		}

		public TitledBorderBuilder<T> justify(int justification) {
			this.justification = justification;
			return this;
		}

		public TitledBorderBuilder<T> position(int position) {
			this.position = position;
			return this;
		}

		public TitledBorderBuilder<T> font(Font font) {
			this.font = font;
			return this;
		}

		public TitledBorderBuilder<T> color(Color color) {
			this.color = color;
			return this;
		}

		public T apply() {
			TitledBorder tb = BorderFactory.createTitledBorder(
				border, title, justification, position, font, color);
			component.setBorder(tb);
			return component;
		}
	}
}

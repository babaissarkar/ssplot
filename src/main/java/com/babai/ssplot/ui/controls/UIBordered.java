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
public interface UIBordered<T extends JComponent> {

	@SuppressWarnings("unchecked")
	default T border(Border b) {
		((JComponent) this).setBorder(b);
		return (T) this;
	}

	default T emptyBorder(int gap) {
		return border(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
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

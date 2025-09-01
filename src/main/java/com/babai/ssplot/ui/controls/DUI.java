/*
 * DUI.java
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
import java.awt.Font;
import java.util.Collection;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

//TODO this does not mentions what property of this class gets bound
public class DUI {
	public static <T, U> U[] forEach(
			Collection<T> items,
			IntFunction<? extends U> mapper,
			IntFunction<U[]> arrayCtor)
	{
		return IntStream.range(0, items.size())
				.mapToObj(mapper)
				.toArray(arrayCtor);
	}
	
	public static <T, U> U[] forEach(
			T[] items,
			IntFunction<? extends U> mapper,
			IntFunction<U[]> arrayCtor)
	{
		return IntStream.range(0, items.length)
				.mapToObj(mapper)
				.toArray(arrayCtor);
	}
	
	public static <T> JComponent[] forEach(Collection<T> items, IntFunction<? extends JComponent> mapper) {
		return forEach(items, mapper, JComponent[]::new);
	}

	public static <T> JComponent[] forEach(T[] items, IntFunction<? extends JComponent> mapper) {
		return forEach(items, mapper, JComponent[]::new);
	}

	public static UILabel label() {
		return new UILabel();
	}

	public static UILabel label(String text) {
		return new UILabel().text(text);
	}

	public static UIInput input() {
		return new UIInput();
	}

	public static UIButton button() {
		return new UIButton();
	}
	
	public static JMenuBar menuBar(Component... children) {
		var bar = new JMenuBar();
		for (var child : children) {
			bar.add(child);
		}
		return bar;
	}

	public static UIMenu menu(String text) {
		return new UIMenu().text(text);
	}
	
	public static UIMenuItem item(String text) {
		return new UIMenuItem().text(text);
	}
	
	public static UIRadioItem radioItem(String text) {
		return new UIRadioItem().text(text);
	}

	public static <E> UIRadioGroup<E> radioGroup(Class<E> clazz) {
		return new UIRadioGroup<>();
	}

	public static JScrollPane scrollPane(JComponent child) {
		return new JScrollPane(
				child,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	public static UIBorderPane borderPane() {
		return new UIBorderPane();
	}

	public static UIHBox hbox(Component... children) {
		var hbox = new UIHBox();
		for (var child : children) {
			hbox.add(child);
		}
		return hbox;
	}

	public static UIVBox vbox(Component... children) {
		var vbox = new UIVBox();
		for (var child : children) {
			vbox.add(child);
		}
		return vbox;
	}

	public static UIGrid grid() {
		return new UIGrid();
	}
	
	public static UITabPane tabPane() {
		return new UITabPane();
	}
	
	public static UISplitPane splitPane() {
		return new UISplitPane();
	}

	public static JToolBar toolbar(Component... children) {
		var toolbar = new JToolBar();
		for (var child : children) {
			toolbar.add(child);
		}
		return toolbar;
	}

	public static UIFrame iframe() {
		return new UIFrame();
	}
	
	public static UIFrame iframe(String title) {
		return iframe().title(title);
	}
	
	public class Text {
		
		public static String bold(String text) {
			return htmlAndBody(tag("b", text));
		}
		
		public static String htmlAndBody(String text) {
			return tag("html", tag("body", text));
		}
		
		public static String tag(String tagName, String text) {
			return "<%s>%s</%s>".formatted(tagName, text, tagName);
		}
		
		public static String tag(String tagName, String attribute, String text) {
			return "<%s %s>%s</%s>".formatted(tagName, attribute, text, tagName);
		}
		
		public final static String LBREAK = "<br/>";
		
		// Fonts
		public final static Font headerFont = new Font("Cantarell", Font.BOLD, 20);
		public final static Font monoFont = new Font("monospace", Font.PLAIN, 14);
	}
}

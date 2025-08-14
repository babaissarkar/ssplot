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
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

//TODO this does not mentions what property of this class gets bound
public class DUI {
	public static <T, U> U[] forEach(
		T[] items,
		IntFunction<? extends U> mapper,
		IntFunction<U[]> arrayCtor)
	{
		return IntStream.range(0, items.length)
		                .mapToObj(mapper)
		                .toArray(arrayCtor);
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
	
	public static JToolBar toolbar(JComponent... children) {
		var toolbar = new JToolBar();
		for (var child : children) {
			toolbar.add(child);
		}
		return toolbar;
	}

	public static UIFrame iframe() {
		return new UIFrame();
	}
}

package com.babai.ssplot.ui.controls;

import java.awt.Component;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

public class DUI {
	public static <T> JComponent[] forEach(T[] items, IntFunction<? extends JComponent> mapper) {
		return IntStream.range(0, items.length)
		                .mapToObj(mapper)
		                .toArray(JComponent[]::new);
	}

	public static UIInput input() {
		return new UIInput();
	}
	
	public static JLabel label(String text) {
		var label = new JLabel(text);
		// for now, labels are left aligned inside layout manager by default
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
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
	
	public static UIHBox hbox(JComponent... children) {
		var hbox = new UIHBox();
		for (var child : children) {
			hbox.add(child);
		}
		return hbox;
	}
	
	public static UIVBox vbox(JComponent... children) {
		var vbox = new UIVBox();
		for (var child : children) {
			vbox.add(child);
		}
		return vbox;
	}
	
	public static JToolBar toolbar(JComponent... children) {
		var toolbar = new JToolBar();
		for (var child : children) {
			toolbar.add(child);
		}
		return toolbar;
	}
}

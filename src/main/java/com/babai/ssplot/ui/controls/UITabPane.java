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

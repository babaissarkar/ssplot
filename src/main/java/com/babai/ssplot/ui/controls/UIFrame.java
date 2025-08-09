package com.babai.ssplot.ui.controls;

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

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
				e.printStackTrace();
			}
		});
		return this;
	}
}

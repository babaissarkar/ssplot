package com.babai.ssplot.ui.controls;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

public class UIGrid extends JPanel implements UIBordered<UIGrid> {
	private GridBagConstraints gbc;
	
	public UIGrid() {
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		setLayout(new GridBagLayout());
	}
	
	public UIGrid anchor(int anchor) {
		gbc.anchor = anchor;
		return this;
	}
	
	public UIGrid fill(int fill) {
		gbc.fill = fill;
		return this;
	}
	
	public UIGrid insets(Insets insets) {
		gbc.insets = insets;
		return this;
	}
	
	public UIGrid weightx(double weightx) {
		gbc.weightx = weightx;
		return this;
	}
	
	public UIGrid weighty(double weighty) {
		gbc.weighty = weighty;
		return this;
	}
	
	public UIGrid row() {
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy++;
		return this;
	}
	
	public UIGrid column(Component comp) {
		add(comp, gbc);
		gbc.gridx++;
		return this;
	}
}

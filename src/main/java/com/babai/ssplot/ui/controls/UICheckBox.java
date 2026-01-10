package com.babai.ssplot.ui.controls;

import javax.swing.JCheckBox;

public class UICheckBox extends JCheckBox
	implements UIStylizable<UICheckBox>, UIStateful<UICheckBox>
{
	public UICheckBox text(String text) {
		setText(text);
		return this;
	}
	
	public UICheckBox selected(boolean sel) {
		setSelected(sel);
		return this;
	}
	
	public UICheckBox onClick(Runnable action) {
		addActionListener(e -> action.run());
		return this;
	}
	
	// Binding type: text StateVar -> JCheckBox's selection (on/off)
	public UICheckBox bindSelection(StateVar<Boolean> selection) {
		setSelected(selection.get());
		selection.onChange(selected -> setSelected(selected));
		return this;
	}
}


package com.babai.ssplot.ui.controls;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class UIRadioGroup<E> extends UIHBox {
	private StateVar<E> selected = null;
	private ButtonGroup group = new ButtonGroup();
	
	public UIRadioGroup<E> options(E[] values, E selected) {
		if (values == null) return this;
		
		this.selected = new StateVar<>(selected);
		
		for (int i = 0; i < values.length; i++) {
			var radio = new JRadioButton(values[i].toString());
			group.add(radio);
			add(radio);
			
			if (values[i] == selected) {
				this.selected.set(selected);
				radio.setSelected(true);
			}
			
			final int idx = i;
			radio.addActionListener(e -> {
				if (radio.isSelected()) {
					this.selected.set(values[idx]);
				}
			});
		}
		
		// no match found or null passed as selected
		if (selected == null) {
			this.selected.set(values[0]);
		}
		return this;
	}
	
	// Binding type: this class -> selection StateVar
	public UIRadioGroup<E> bindFromUI(StateVar<E> selection) {
		if (selection != null) {
			this.selected = selection;
		}
		return this;
	}
}

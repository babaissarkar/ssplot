package com.babai.ssplot.ui.controls;

import javax.swing.JComponent;

@SuppressWarnings("unchecked")
public interface UIStateful<T extends JComponent> {
	
	// statevar change -> enabled property change
	default T enabled(StateVar<Boolean> enabled) {
		((JComponent) this).setEnabled(enabled.get());
		enabled.onChange(e -> ((JComponent) this).setEnabled(e));
		return (T) this;
	}

	default T enabled(boolean enabled) {
		((JComponent) this).setEnabled(enabled);
		return (T) this;
	}

	// statevar change -> visible property change
	default T visible(StateVar<Boolean> visible) {
		((JComponent) this).setVisible(visible.get());
		visible.onChange(v -> ((JComponent) this).setVisible(v));
		return (T) this;
	}

	default T visible(boolean visible) {
		((JComponent) this).setVisible(visible);
		return (T) this;
	}
}

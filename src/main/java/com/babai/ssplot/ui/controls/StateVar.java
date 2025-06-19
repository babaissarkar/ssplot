package com.babai.ssplot.ui.controls;

public class StateVar<T> {
	T value;
	Runnable onChange = null;
	
	public StateVar(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
	public void set(T value) {
		this.value = value;
		if (onChange != null) {
			onChange.run();
		}
	}
	
	public void bind(Runnable r) {
		this.onChange = r;
	}
}

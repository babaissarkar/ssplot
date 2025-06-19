package com.babai.ssplot.ui.controls;

import java.util.function.Function;

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
	
	public <U> StateVar<U> derive(Function<T, U> mapper) {
		StateVar<U> derived = new StateVar<>(mapper.apply(this.get()));
		bind(() -> derived.set(mapper.apply(get())));
		return derived;
	}

}

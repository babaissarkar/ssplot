package com.babai.ssplot.ui.controls;

import java.util.Vector;
import java.util.function.Function;

public class StateVar<T> {
	private T value;
	private Vector<Runnable> runners = new Vector<>();
	
	public StateVar(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
	public void set(T value) {
		this.value = value;
		for (var runner : runners) {
			runner.run();
		}
	}
	
	public void bind(Runnable r) {
		this.runners.add(r);
	}
	
	public void unbindAll() {
		this.runners.clear();
	}
	
	public <U> StateVar<U> when(Function<T, U> mapper) {
		StateVar<U> derived = new StateVar<>(mapper.apply(get()));
		bind(() -> derived.set(mapper.apply(get())));
		return derived;
	}

}

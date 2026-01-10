/*
 * StateVar.java
 * 
 * Copyright 2025 Subhraman Sarkar <suvrax@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

package com.babai.ssplot.ui.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class StateVar<T> {
	private T value;
	private List<Consumer<T>> runners = new ArrayList<>();
	
	public StateVar(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
	public void set(T value) {
		this.value = value;
		for (var runner : runners) {
			runner.accept(value);
		}
	}
	
	public void onChange(Consumer<T> r) {
		this.runners.add(r);
	}
	
	public void unbindAll() {
		this.runners.clear();
	}
	
	public <U> StateVar<U> when(Function<T, U> mapper) {
		StateVar<U> derived = new StateVar<>(mapper.apply(get()));
		onChange(val -> derived.set(mapper.apply(val)));
		return derived;
	}
	
	public StateVar<Boolean> whenAny(List<T> values) {
		return this.when(val -> {
			boolean res = false;
			for (var v : values) {
				if (v.equals(val)) {
					res = true;
				}
			}
			return res;
		});
	}

}

/*
 * StateVar.java
 * 
 * Copyright 2025-2026 Subhraman Sarkar <suvrax@gmail.com>
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
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
		if (Objects.equals(this.value, value)) {
			return;
		}
		
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
	
	// RULE: when() predicates must only read StateVars
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
	
	public static <A, B, R> StateVar<R> combine(
		StateVar<A> a,
		StateVar<B> b,
		BiFunction<A, B, R> fn
	) {
		StateVar<R> out = new StateVar<>(fn.apply(a.get(), b.get()));

		final AtomicReference<A> av = new AtomicReference<>(a.get());
		final AtomicReference<B> bv = new AtomicReference<>(b.get());

		a.onChange(v -> {
			av.set(v);
			out.set(fn.apply(av.get(), bv.get()));
		});

		b.onChange(v -> {
			bv.set(v);
			out.set(fn.apply(av.get(), bv.get()));
		});

		return out;
	}
	
	@FunctionalInterface
	public interface TriFunction<A, B, C, R> {
		R apply(A a, B b, C c);
	}
	
	public static <A, B, C, R> StateVar<R> combine(
		StateVar<A> a,
		StateVar<B> b,
		StateVar<C> c,
		TriFunction<A, B, C, R> fn
	) {
		StateVar<R> out = new StateVar<>(fn.apply(a.get(), b.get(), c.get()));

		final AtomicReference<A> av = new AtomicReference<>(a.get());
		final AtomicReference<B> bv = new AtomicReference<>(b.get());
		final AtomicReference<C> cv = new AtomicReference<>(c.get());

		a.onChange(v -> {
			av.set(v);
			out.set(fn.apply(av.get(), bv.get(), cv.get()));
		});

		b.onChange(v -> {
			bv.set(v);
			out.set(fn.apply(av.get(), bv.get(), cv.get()));
		});

		c.onChange(v -> {
			cv.set(v);
			out.set(fn.apply(av.get(), bv.get(), cv.get()));
		});

		return out;
	}

}

/*
 * SystemMode.java
 * 
 * Copyright 2023-2025 Subhraman Sarkar <suvrax@gmail.com>
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

package com.babai.ssplot.math.system.core;

public enum SystemMode {
	ODE("Differential Equation"),
	DFE("Difference Equation"),
	FN2D("2D function"),
	FN3D("3D function");

	private String name;
	
	SystemMode(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
};

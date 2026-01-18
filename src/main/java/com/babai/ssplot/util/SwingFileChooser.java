/*
 * SwingFileChooser.java
 *
 * Copyright 2026 Subhraman Sarkar <suvrax@gmail.com>
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
 */

package com.babai.ssplot.util;

import java.awt.Component;
import java.nio.file.Path;
import java.util.Optional;

import javax.swing.JFileChooser;

/** File open/save UI helper utility */
public final class SwingFileChooser {
	private static final JFileChooser files = new JFileChooser();
	
	private SwingFileChooser() {}

	public static Optional<Path> open(Component parent) {
		if (files.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			Path dpath = files.getSelectedFile().toPath();
			return dpath != null ? Optional.of(dpath) : Optional.empty();
		} else {
			return Optional.empty();
		}
	}

	public static Optional<Path> save(Component parent) {
		if (files.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			Path dpath = files.getSelectedFile().toPath();
			return dpath != null ? Optional.of(dpath) : Optional.empty();
		} else {
			return Optional.empty();
		}
	}
}

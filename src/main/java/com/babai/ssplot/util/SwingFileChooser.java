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

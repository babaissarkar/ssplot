/*
 * FocusTracker.java
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
 */

package com.babai.ssplot.util;

import java.awt.KeyboardFocusManager;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * Utility class for globally tracking the last focused text component
 * (JTextField, JTextArea, etc.) in a Swing application and allowing
 * text insertion into it.
 *
 * <p>How it works:</p>
 * <ul>
 *   <li>Call {@link #install()} once during application startup.
 *       This attaches a listener to the global KeyboardFocusManager.</li>
 *   <li>Whenever a text component gains permanent focus, it is remembered.</li>
 *   <li>Call {@link #insertText(String)} to insert text into the currently
 *       focused text component (if any). The inserted text will replace the
 *       current selection, or be placed at the caret position.</li>
 * </ul>
 *
 * <p>This is especially useful for "palette" or "virtual keyboard" UIs
 * (e.g. math symbol pickers) that need to send characters or snippets
 * into whichever input field the user is working on.</p>
 */
public class FocusTracker {
	private static JTextComponent lastFocused;

	public static void install() {
		KeyboardFocusManager
			.getCurrentKeyboardFocusManager()
			.addPropertyChangeListener("permanentFocusOwner", e -> {
				if (e.getNewValue() instanceof JTextComponent) {
					lastFocused = (JTextComponent) e.getNewValue();
				}
			});
	}

	public static void insertText(String text) {
		if (lastFocused != null) {
			lastFocused.replaceSelection(text); // inserts at caret or replaces selection
			// Return focus back asynchronously so button click doesn't override it
			SwingUtilities.invokeLater(lastFocused::requestFocusInWindow);
		}
	}
	
	public static void insertTextWithCaret(String text, int caretOffset) {
		if (lastFocused != null) {
			int pos = lastFocused.getCaretPosition();
			lastFocused.replaceSelection(text);
			int newCaretPos = pos + caretOffset;
			SwingUtilities.invokeLater(() -> {
				lastFocused.requestFocusInWindow();
				lastFocused.setCaretPosition(newCaretPos);
			});
		}
	}

}


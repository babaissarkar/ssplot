/*
 * StatLogger.java
 * 
 * Copyright 2021-2025 Subhraman Sarkar <suvrax@gmail.com>
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

package com.babai.ssplot.ui;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import com.babai.ssplot.ui.controls.DUI.Text;
import com.babai.ssplot.util.InfoLogger;

public class StatLogger implements InfoLogger {
	private StringBuffer logs = new StringBuffer();
	private JTextPane txtStatus;
	private JScrollPane jscroll;
	
	public StatLogger() {
		txtStatus = new JTextPane();
		txtStatus.setContentType("text/html");
		txtStatus.setEditable(false);

		jscroll = new JScrollPane(txtStatus,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	public void log(String s) {
		String log = s.strip();
		log = log.replace("<html>", "");
		log = log.replace("<body>", "");
		log = log.replace("</body>", "");
		log = log.replace("</html>", "");
		
		logs.append(log + Text.LBREAK);
		txtStatus.setText(Text.htmlAndBody(logs.toString()));
		txtStatus.setCaretPosition(txtStatus.getDocument().getLength());
	}
	
	public Component getComponent() {
		return this.jscroll;
	}

	public void clear() {
		logs = new StringBuffer();
		txtStatus.setText(Text.htmlAndBody(logs.toString()));
		txtStatus.setCaretPosition(txtStatus.getDocument().getLength());
	}
	
}

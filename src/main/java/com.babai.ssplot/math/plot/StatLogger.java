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

package math.plot;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

public class StatLogger {
	private StringBuffer logs = new StringBuffer();
	private JTextPane txStatus;
	private JScrollPane jscroll;
	
	public StatLogger() {
		txStatus = new JTextPane();
		txStatus.setContentType("text/html");
        txStatus.setEditable(false);        

        jscroll = new JScrollPane(txStatus,
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	public void log(String s) {
		logs.append(s + "<br>");
		txStatus.setText("<html><body>" + logs.toString() + "</body></html>");
	}
	
	public Component getComponent() {
		return this.jscroll;
	}

	public void clear() {
		logs = new StringBuffer();
		txStatus.setText("<html><body>" + logs.toString() + "</body></html>");
	}
	
}

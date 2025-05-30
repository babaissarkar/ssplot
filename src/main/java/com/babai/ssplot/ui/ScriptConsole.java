/*
 * ScriptConsole.java
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

package com.babai.ssplot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** TODO : needs to be a real terminal, with input and output both handled by it */
public class ScriptConsole extends JPanel {
	private JTextField txtIn;
	private JLabel lblOut;
	private JButton btnRun;
	private ScriptEngine engine;
	private final String defaultEngine = "rhino";
	private String initScript;
	
	
	public ScriptConsole() {
		initEngine(defaultEngine);
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		
		txtIn = new JTextField(60);
		txtIn.setHorizontalAlignment(JTextField.CENTER);
		txtIn.setMargin(new Insets(5, 5, 5, 5));
		txtIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtIn.getPreferredSize().height));
		
		lblOut = new JLabel() {
			private String input = "";
			private String output = "";
			private String current = ""; // Value of last expression
			private boolean isError = false; // Did the last evaluation succeed?

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setFont(g2.getFont().deriveFont(16f));
				g2.addRenderingHints(new RenderingHints(
						RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
				g2.addRenderingHints(new RenderingHints(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON));
				FontMetrics fm = g2.getFontMetrics();
				int textH = fm.getHeight();
				
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, getSize().width, getSize().height);
				if (!input.isEmpty()) {
					g2.setColor(Color.WHITE);
					g2.drawString(">> " + input, 20, 20);
					g2.setColor(Color.YELLOW);
					String temp = "[" + this.current + "]";
					int size = fm.stringWidth(temp);
					g2.drawString(temp, 20, 25+textH);
					g2.setColor(isError ? Color.RED : Color.GREEN);
					g2.drawString(output, 25+size, 25+textH);
					g2.setColor(Color.WHITE);
				}
			}
			
			private void clearVariables() {
				output = "";
				current = "";
				engine.put("txt", "");
			}
			@Override
			public void setText(String input) {
				clearVariables();
				
				this.input = input;
				try {
					current = engine.eval(initScript + input).toString();
					Object out = engine.get("txt");
					if (out != null) {
						output = out.toString();
					}
				} catch (ScriptException e) {
					output = "Error!";
				}
				repaint();
			}
		};
		lblOut.setPreferredSize(new Dimension(size.width - 10, 100));
		
		btnRun = new JButton("Run");
		btnRun.setIcon(new ImageIcon(getClass().getResource("/run.png")));
		btnRun.setBackground(Color.GREEN);
		btnRun.addActionListener(e -> lblOut.setText(txtIn.getText()));
		
		setLayout(new BorderLayout());
		JPanel pnlInput = new JPanel();
		pnlInput.setLayout(new BoxLayout(pnlInput, BoxLayout.LINE_AXIS));
		pnlInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pnlInput.add(txtIn);
		pnlInput.add(Box.createHorizontalStrut(5));
		pnlInput.add(btnRun);
		
		JPanel pnlOutput = new JPanel();
		pnlOutput.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlOutput.add(lblOut);
		
		this.add(pnlInput, BorderLayout.NORTH);
		this.add(pnlOutput, BorderLayout.CENTER);
	}
	
	public void focusInput() {
		txtIn.requestFocusInWindow();
	}
	
	private void initEngine(String engineName) {
		ScriptEngineManager m = new ScriptEngineManager();
		
		// TODO load these methods from a initial script file
		// Add print method	
		initScript = """
			function debugPrint(value) {
				java.lang.System.out.print(value);
			}
			
			function debugPrintLn(value) {
				java.lang.System.out.println(value);
			}
	
			function print(value) {
				txt = value;
			}
		""";
		
		engine = m.getEngineByName(engineName);
	}
}

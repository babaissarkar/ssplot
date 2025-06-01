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
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.babai.ssplot.math.system.parser.Parser;
import com.babai.ssplot.math.system.parser.ParserManager;

// TODO : needs to be a real terminal, with input and output both handled by it
// TODO : the initscript is not being used, partly because difference parsing
// backends need different code.
public class ScriptConsole extends JPanel {
	private JTextField txtIn;
	private JLabel lblOut;
	private JButton btnRun;
	private Parser parser;
//	private String initScript;
	
	public ScriptConsole() {
		initEngine();
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		
		txtIn = new JTextField(60);
		txtIn.setHorizontalAlignment(JTextField.CENTER);
		txtIn.setMargin(new Insets(5, 5, 5, 5));
		txtIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtIn.getPreferredSize().height));
		
		lblOut = new JLabel() {
			private String input = "";
			private String output = "";
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
					g2.setColor(Color.YELLOW);
					String header = "(" + parser.getName() + ")";
					int headerWidth = fm.stringWidth(header);
					g2.drawString(header, 20, 20);
					g2.setColor(Color.WHITE);
					g2.drawString(">> " + input, headerWidth + 20, 20);
					g2.setColor(isError ? Color.RED : Color.GREEN);
					g2.drawString(output, 20, 25 + textH);
					g2.setColor(Color.WHITE);
				}
			}
			
			private void clearVariables() {
				output = "";
			}
			@Override
			public void setText(String input) {
				clearVariables();
				this.input = input;
				double res = parser.evaluate(/* initScript + */ input, Map.of());
				output = "" + res;
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
	
	private void initEngine() {
		// TODO load these methods from a initial script file
		// Add print method	
//		initScript = """
//			function debugPrint(value) {
//				java.lang.System.out.print(value);
//			}
//			
//			function debugPrintLn(value) {
//				java.lang.System.out.println(value);
//			}
//	
//			function print(value) {
//				txt = value;
//			}
//		""";
		
		parser = ParserManager.getParser();
	}
}

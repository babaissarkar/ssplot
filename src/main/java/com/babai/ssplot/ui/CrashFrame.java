/*
 * CrashFrame.java
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

package com.babai.ssplot.ui;

import javax.swing.*;

import com.babai.ssplot.ui.controls.DUI.Text;
import com.babai.ssplot.util.SystemInfo;

import static com.babai.ssplot.util.UIHelper.openLink;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CrashFrame extends JFrame {

	public CrashFrame(String crashLog) {
		setTitle("SSPlot Error");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(null); // center on screen
		setLayout(new BorderLayout(10, 10));

		// Icon and message panel
		JPanel topPanel = new JPanel(new BorderLayout(10, 10));
		JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
		topPanel.add(iconLabel, BorderLayout.WEST);

		JLabel messageLabel = new JLabel(Text.htmlAndBody(
				"Oops! Something went wrong."
				+ Text.LBREAK
				+ "Please report this issue so we can fix it. Thank you!"));
		topPanel.add(messageLabel, BorderLayout.CENTER);
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(topPanel, BorderLayout.NORTH);

		// Crash log text area
		JTextArea logArea = new JTextArea(crashLog);
		logArea.setEditable(false);
		logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(logArea);
		add(scrollPane, BorderLayout.CENTER);

		// Buttons panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		JButton copyButton = new JButton("Copy Error");
		JButton reportButton = new JButton("Report Issue");

		// Copy error to clipboard
		copyButton.addActionListener(e -> {
			StringSelection selection = new StringSelection(logArea.getText());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
			JOptionPane.showMessageDialog(
				this,
				"Error log copied to clipboard!",
				"Copied",
				JOptionPane.INFORMATION_MESSAGE);
		});

		// Open prefilled report issue link
		reportButton.addActionListener(e -> {
			String issueURL = "https://github.com/babaissarkar/ssplot/issues/new?"
				+ "title="
				+ URLEncoder.encode("[BUG] SSPlot Crash (Edit me)", StandardCharsets.UTF_8)
				+ "&body="
				+ URLEncoder.encode(
					"\n\n" + SystemInfo.getSystemInfo() + "\n"
					+ "```java\n" + logArea.getText() + "\n```",
					StandardCharsets.UTF_8)
				+ "&labels=bug";
			openLink(issueURL);
		});

		buttonPanel.add(copyButton);
		buttonPanel.add(reportButton);
		add(buttonPanel, BorderLayout.SOUTH);

		setVisible(true);
	}

	/**
	 * Static factory method to show crash log from a Throwable.
	 * Works like e.printStackTrace() but shows a GUI.
	 */
	public static void showCrash(Throwable e) {
		// Convert stack trace to string
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stackTrace = sw.toString();

		// Show frame on EDT
		SwingUtilities.invokeLater(() -> new CrashFrame(stackTrace));
	}
}

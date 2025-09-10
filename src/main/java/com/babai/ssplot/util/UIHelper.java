/*
 * UIHelper.java
 * 
 * Copyright 2025 Subhraman Sarkar <suvrax@gmail.com>
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

package com.babai.ssplot.util;

import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import com.babai.ssplot.ui.CrashFrame;
import com.babai.ssplot.ui.MainFrame;
import com.babai.ssplot.ui.controls.DUI;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.util.SystemInfo;

public interface UIHelper {
	
	/* Load FlatLaf UI properties */
	public static void loadUIProperties() {
		Properties prop = new Properties();
		try {
			prop.load(MainFrame.class.getResourceAsStream("/com/babai/ssplot/ui/FlatLaf.properties"));
			// Load color/integer objects from strings
			prop.forEach((key, value) -> { 
				String val = value.toString();
				if (val.startsWith("#")) {
					UIManager.put(key, Color.decode(val));
				} else {
					try {
						UIManager.put(key, Integer.parseInt(val));
					} catch (Exception e) {
						UIManager.put(key, val);
					}
				}
			});
		} catch (IOException e) {
			CrashFrame.showCrash(e);
		}
	}
	
	private static void setUIFont(Font font) {
		var keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof Font) {
				UIManager.put(key, font);
			}
		}
	}
	
	public static void errorMsg(String title, String msg) {
		JOptionPane.showMessageDialog(
			null,        // e.g., 'null' for centered on screen
			msg, 
			title,                // dialog title
			JOptionPane.ERROR_MESSAGE);
	}
	
	/* Open a URL using platform's browser */
	public static void openLink(String url) {
		if (url.isBlank()) {
			errorMsg("openLink error", "Empty link, not opening.");
			return;
		}
		
		if (!Desktop.isDesktopSupported()) {
			errorMsg("openLink error", "Browsing links not supported on this platform!");
			return;
		}
		
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (URISyntaxException|IOException e) {
			//FIXME on very rare cases this can cause a circular loop
			//such as if the CrashFrame's report issue button calls this with wrong url
			//but that means someone tampered with the code, so we let this be.
			CrashFrame.showCrash(e);
		}
	}
	
	/* Keybinding management helper */
	public static void bindAction(JComponent control, String actionName, String hotkey, Runnable action) {
		control.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		       .put(KeyStroke.getKeyStroke(hotkey), actionName);
		control.getActionMap().put(actionName, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				action.run();
			}
		});
	}
	
	public static void setNimbusLF() {
		for (LookAndFeelInfo lafinf : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(lafinf.getName())) {
				try {
					UIManager.setLookAndFeel(lafinf.getClassName());
				} catch (ClassNotFoundException
					| InstantiationException
					| IllegalAccessException
					| UnsupportedLookAndFeelException e)
				{
					CrashFrame.showCrash(e);
					setMetalLF();
				}
			}
		}
		setUIFont(DUI.Text.baseFont);
	}
	
	public static void setMetalLF() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException
			| InstantiationException
			| IllegalAccessException
			| UnsupportedLookAndFeelException e)
		{
			CrashFrame.showCrash(e);
		}
		setUIFont(DUI.Text.baseFont);
	}

	public static void setDarkLF() {
		FlatArcDarkOrangeIJTheme.setup();
		
		if (SystemInfo.isLinux) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
		UIManager.put("defaultFont", DUI.Text.baseFont);
	}

	public static void setLightLF() {
		FlatArcOrangeIJTheme.setup();
		
		if (SystemInfo.isLinux) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
		UIManager.put("defaultFont", DUI.Text.baseFont);
	}
}

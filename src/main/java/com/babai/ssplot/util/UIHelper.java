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
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import com.babai.ssplot.ui.MainFrame;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.util.SystemInfo;

public interface UIHelper {
	
	/* Load FlatLaf UI properties */
	public static void loadUIProperties() {
		Properties prop = new Properties();
		try {
			prop.load(MainFrame.class.getResourceAsStream("/com/babai/ssplot/ui/FlatLaf.properties"));
			// After loading properties (customProps)
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
			e.printStackTrace();
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
					e.printStackTrace();
					setMetalLF();
				}
			}
		}
	}
	
	public static void setMetalLF() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException
			| InstantiationException
			| IllegalAccessException
			| UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
	}

	public static void setDarkLF() {
		FlatArcDarkOrangeIJTheme.setup();
		
		if (SystemInfo.isLinux) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
	}

	public static void setLightLF() {
		FlatArcOrangeIJTheme.setup();
		
		if (SystemInfo.isLinux) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
	}
}

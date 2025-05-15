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

package util;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.util.SystemInfo;

public interface UIHelper {
	
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

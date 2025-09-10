package com.babai.ssplot.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.babai.ssplot.ui.CrashFrame;
import com.babai.ssplot.ui.MainFrame;

import static com.babai.ssplot.ui.controls.DUI.Text.*;

public class SystemInfo {

	public static String getSystemInfo() {
		StringBuilder sb = new StringBuilder();
		Properties props = System.getProperties();

		sb.append(tag("h1", "System Information"))
		.append(LBREAK);
		
		// Version
		final Properties prop = new Properties();
		String versionStr = "unknown";
		try (InputStream is = MainFrame.class.getResourceAsStream("/project.properties")) {
			prop.load(is);
			versionStr = prop.getProperty("version");
		} catch (IOException e) {
			CrashFrame.showCrash(e);
		}
		sb.append(tag("b", "SSPlot Version: ") + versionStr).append(LBREAK);

		// OS details
		sb.append(tag("b", "OS: ")).append(props.getProperty("os.name"))
		.append(" ").append(props.getProperty("os.version"))
		.append(" (").append(props.getProperty("os.arch")).append(")")
		.append(LBREAK);

		// Java details
		sb.append(tag("b", "Java version: ")).append(props.getProperty("java.version"))
		.append(" (").append(props.getProperty("java.vendor")).append(")")
		.append(LBREAK);

		// Memory info
		long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
		long freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024);
		long maxMem = Runtime.getRuntime().maxMemory() / (1024 * 1024);
		sb.append(tag("b", "Memory (MB):"))
		.append(" Used: ").append(totalMem - freeMem)
		.append(" / Total: ").append(totalMem)
		.append(" / Max: ").append(maxMem).append(LBREAK);

		// Distribution info
		sb.append(tag("b", "Distribution: "))
		.append(getDistributionInfo()).append(LBREAK);

		return sb.toString();
	}

	private static String getDistributionInfo() {
		if (System.getenv("SNAP") != null) {
			return "Snap (" + System.getenv("SNAP_NAME") +
					" rev " + System.getenv("SNAP_REVISION") + ")";
		}
		if (System.getenv("FLATPAK_ID") != null) {
			return "Flatpak (" + System.getenv("FLATPAK_ID") + ")";
		}
		if (System.getenv("APPIMAGE") != null) {
			return "AppImage (" + System.getenv("APPIMAGE") + ")";
		}
		return "Direct/JAR install (e.g. GitHub release)";
	}
}


package math.plot;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public class ScriptConsole extends JPanel {
	private ConsoleLabel lblOut;

	
	public ScriptConsole() {		
		lblOut = new ConsoleLabel();
		lblOut.setPreferredSize(new Dimension(800, 100));
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(lblOut);
	}
}

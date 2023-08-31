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

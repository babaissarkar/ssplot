package math.plot;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
//import javax.swing.text.DefaultCaret;

public class StatLogger {
	private StringBuffer logs = new StringBuffer();
	private JFrame frmLog;
	private JTextPane txStatus;
	private JScrollPane jscroll;
	
	public StatLogger() {
		frmLog = new JFrame("Logs");
		//frmLog.setSize(new Dimension(500, 100));
		frmLog.setBounds(750, 550, 500, 200);
		txStatus = new JTextPane();
		txStatus.setContentType("text/html");
		txStatus.setPreferredSize(new Dimension(frmLog.getWidth(), 100));
        txStatus.setEditable(false);
       
        JPanel pnlStatus = new JPanel();

        jscroll = new JScrollPane(txStatus,
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        pnlStatus.add(jscroll);
        frmLog.setContentPane(pnlStatus);
        frmLog.setResizable(false);
        frmLog.pack();
	}
	
	public void showLogs() {
		txStatus.setText("<html><body>" + logs.toString() + "</body></html>");
		
		SwingUtilities.updateComponentTreeUI(frmLog);
		
		if (!frmLog.isVisible()) {
			frmLog.setVisible(true);
		}
	}

	public void log(String s) {
		//frmLog.setVisible(false);
		logs.append(s + "<br>");
		showLogs();
	}
	
}

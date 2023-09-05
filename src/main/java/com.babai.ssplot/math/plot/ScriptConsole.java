package math.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ScriptConsole extends JPanel {
	private JTextArea txtIn;
//	private JTextArea txtOut;
	private JLabel lblOut;
	private JButton btnRun;
	private ScriptEngine engine;
	private final String defaultEngine = "rhino";
	
	
	public ScriptConsole() {
		initEngine(defaultEngine);
		
		txtIn = new JTextArea(4, 40);
//		txtOut = new JTextArea(4, 40);
		lblOut = new JLabel() {
			private String input = "";
			private String output = "";
//			private String textOut;
			
			@Override
			public void paint(Graphics g) {
//				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				RenderingHints rh = new RenderingHints(
						RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.addRenderingHints(rh);
				FontMetrics fm = g2.getFontMetrics();
				int textH = fm.getHeight();
				
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, 1000, 200);
				if (!input.equals("")) {
					g2.setColor(Color.WHITE);
					g2.drawString(">> " + input, 20, 20);
					g2.setColor(Color.GREEN);
					g2.drawString(output, 20, 25 + textH);
					g2.setColor(Color.WHITE);
				}
			}
			
			@Override
			public void setText(String input) {
				this.input = input;
				try {
					this.output = engine.eval(input).toString();
				} catch (ScriptException e) {
					this.output = "Error!";
				}
				this.repaint();
			}
		};
		btnRun = new JButton("Run");
		btnRun.addActionListener(
			e -> {
//					lblOut.setText("<html><span style='color:green'> >> " + input + "</span><br>" + obj.toString() + "</html>");
					lblOut.setText(txtIn.getText());
			}
		);
		
//		txtIn.setHorizontalAlignment(JTextField.CENTER);
		lblOut.setPreferredSize(new Dimension(1000, 60));
//		lblOut.setFont(lblOut.getFont().deriveFont(18.0f));
//		lblOut.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JPanel pnlInput = new JPanel();
		pnlInput.setLayout(new FlowLayout(FlowLayout.CENTER));
		JScrollPane sIn = new JScrollPane(
				txtIn,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pnlInput.add(sIn);
		pnlInput.add(btnRun);
		JPanel pnlOutput = new JPanel();
		pnlOutput.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlOutput.add(lblOut);
		this.add(pnlInput);
		this.add(pnlOutput);
	}
	
	public void initEngine(String engineName) {
		ScriptEngineManager m = new ScriptEngineManager();
		engine = m.getEngineByName(engineName);
	}
}

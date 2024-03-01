package math.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** TODO : needs to be a real terminal, with input and output both handled by it */
public class ScriptConsole extends JPanel {
	private JTextArea txtIn;
//	private JTextArea txtOut;
	private JLabel lblOut;
	private JButton btnRun;
	private ScriptEngine engine;
	private final String defaultEngine = "rhino";
	private String initScript;
	
	
	public ScriptConsole() {
		initEngine(defaultEngine);
		
		txtIn = new JTextArea(2, 60);
		lblOut = new JLabel() {
			private String input = "";
			private String output = "";
			private String current = ""; // Value of last JS expression

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
					g2.setColor(Color.YELLOW);
					String temp = "[" + this.current + "]";
					int size = fm.stringWidth(temp);
					g2.drawString(temp, 20, 25+textH);
					g2.setColor(Color.GREEN);
					g2.drawString(output, 25+size, 25+textH);
					g2.setColor(Color.WHITE);
				}
			}
			
			private void clearVariables() {
				this.output = "";
				this.current = "";
				engine.put("txt", "");
			}
			@Override
			public void setText(String input) {
				clearVariables();
				
				this.input = input;
				try {
					this.current = engine.eval(initScript + input).toString();
					Object out = engine.get("txt");
					if (out!= null) {
						this.output = out.toString();
					}
				} catch (ScriptException e) {
					this.output = "Error!";
				}
				this.repaint();
			}
		};
		btnRun = new JButton("Run");
		btnRun.addActionListener(
			e -> {
				lblOut.setText(txtIn.getText());
			}
		);
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension size = kit.getScreenSize();
		lblOut.setPreferredSize(new Dimension(size.width - 100, 60));
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JPanel pnlInput = new JPanel();
		pnlInput.setLayout(new FlowLayout(FlowLayout.LEFT));
		JScrollPane sIn = new JScrollPane(
				txtIn,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pnlInput.add(sIn);
		pnlInput.add(btnRun);
		JPanel pnlOutput = new JPanel();
		pnlOutput.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlOutput.add(lblOut);
		this.add(pnlInput);
		this.add(pnlOutput);
	}
	
	public void initEngine(String engineName) {
		ScriptEngineManager m = new ScriptEngineManager();
//		SimpleScriptContext con = new SimpleScriptContext();
		
		// Add print method	
		initScript = """
	        function print2(value) {
				    java.lang.System.out.print(value);
	        }
	        
	        function print(value) {
				    txt = value;
	        }
	        
	        function printLn2(value) {
				    java.lang.System.out.println(value);
	        }
	    """;
		
		engine = m.getEngineByName(engineName);
	}
}

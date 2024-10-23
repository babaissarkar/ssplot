package math.plot;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JLabel;

/** TODO : needs to be a real terminal, with input and output both handled by it */
public class ConsoleLabel extends JLabel {
	private ScriptEngine engine;
	private final String defaultEngine = "rhino";
	private StringBuffer input = new StringBuffer();
	private String output = "";
	// TODO we are setting current but not actually using it
	private String current = ""; // Value of last JS expression
	private String initScript;
	
	public ConsoleLabel() {
		initEngine(defaultEngine);
		setFocusable(true);
		
		addMouseListener(
			new MouseAdapter() {		
				@Override
				public void mousePressed(MouseEvent me) {
					requestFocusInWindow();
				}
			}
		);
		
		addKeyListener(
			new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent ke) {
//					System.out.println("Key Typed :" + ke.getKeyChar());
					if (ke.getKeyChar() == '\n') {
//						System.out.println("eval");
						evaluate();
					} else if (ke.getKeyChar() == '\b') {
						String input;
						if (!(input = getInputText()).isEmpty()) {
							setText(input.substring(0, getInputText().length() - 1));
						}
					} else {
						setText(getInputText() + ke.getKeyChar());
					}
				}
			}
		);
		
//		addFocusListener(
//			new FocusAdapter() {
//				@Override
//				public void focusGained(FocusEvent fe) {
//					
//				}
//			}
//		);
		
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.addRenderingHints(rh);
		g2.setFont(g2.getFont().deriveFont(22.0f));
		FontMetrics fm = g2.getFontMetrics();
		int textH = fm.getHeight();
		
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, 1000, 200);
		
		int size = 0;
		g2.setColor(Color.WHITE);
		g2.drawString(">> " + input, 20, 25);
		
		g2.setColor(Color.YELLOW);
		String temp = "Ans: ";
		size = fm.stringWidth(temp);
		g2.drawString(temp, 20, 30+textH);
			
		if (!output.isEmpty()) {
			g2.setColor(Color.GREEN);
			g2.drawString(output, 25+size, 30+textH);
			g2.setColor(Color.WHITE);
		}
	}
	
//	private void clearVariables() {
//		this.output = "";
//		this.current = "";
//		if (engine != null) {
//			engine.put("txt", "");
//		} else {
//			initEngine(defaultEngine);
//		}
//	}
	
	@Override
	public void setText(String input) {
		//clearVariables();
		this.input = new StringBuffer(input);
		this.repaint();
	}
	
	public String getInputText() {
		return this.input.toString();
	}
	
	public void evaluate() {
		try {
			this.current = engine.eval(initScript + input).toString();
			Object out = engine.get("txt");
			if (out != null) {
				this.output = out.toString();
			}
		} catch (ScriptException e) {
			this.output = "Error!";
		}
		this.repaint();
		
		//clearVariables();
	}
	
	public void initEngine(String engineName) {
		ScriptEngineManager m = new ScriptEngineManager();
//		SimpleScriptContext con = new SimpleScriptContext();
		
		// Add print method	
		initScript = """
	        function consolePrint(value) {
				java.lang.System.out.print(value);
	        }
	        
	        function consolePrintLn(value) {
				java.lang.System.out.println(value);
	        }
	        
	        function print(value) {
				txt = value;
	        }
	    """;
		
		engine = m.getEngineByName(engineName);
	}
}

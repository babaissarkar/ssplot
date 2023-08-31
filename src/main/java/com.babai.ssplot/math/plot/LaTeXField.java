package math.plot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JTextField;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

public class LaTeXField extends JTextField {
	private ImageIcon img;
	private boolean showImg = true;
	private String formula;
	
	private final int RENDER_SIZE = 18;
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if ((img != null) && (showImg)) {
			Image im = img.getImage();
			int h = im.getHeight(null);
			int w = im.getWidth(null);
			g.setColor(Color.BLUE);
			g.drawImage(im, 10, 2, null);
			g.setColor(Color.BLACK);
		}
	}
	
	public LaTeXField(int col) {
		super(col);
		
		formula = "";
		img = null;
		this.setHorizontalAlignment(JTextField.CENTER);
		this.setFont(new Font("LMRomanUnsl10-Regular", Font.PLAIN, 16));
		
		this.addFocusListener(new FocusListener() {
        	@Override
        	public void focusGained(FocusEvent fe) {
        		if (!(getText().length() > 0)) {
        			setImageVisible(false);
        		}
        	}

			@Override
			public void focusLost(FocusEvent fe) {
				/* Render the equation and show it */
				if (getText().length() > 0) {
					setImage(render(getText()));
					setImageVisible(true);
				}
			}
        });
	}
	
	public ImageIcon getImage() {
		return this.img;
	}
	
	public void setImage(ImageIcon img) {
		this.img = img;
	}
	
	public void setImageVisible(boolean visible) {
		this.showImg = visible;
		if (visible) {
			formula = this.getText();
    		this.setText("");
		} else {
			this.setText(formula);
			formula = "";
		}
		this.repaint();
	}
	
	public ImageIcon render(String formula) {
    	TeXFormula f = new TeXFormula(formula);
        ImageIcon ico = new ImageIcon(
        		f.createBufferedImage(
        			TeXConstants.STYLE_DISPLAY, RENDER_SIZE, Color.black, null));
        return ico;
    }
	
	public String getFormulaText() {
		if (formula != "") {
			return formula;
		} else {
			return this.getText();
		}
	}
}

package main.java;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class MBGui extends JFrame implements ActionListener, MouseListener, KeyListener {

	/**
	 * Mandelbrot Set
	 * Geometric Demonstration
	 * TCU Physics
	 * 12/5/2016
	 * @author Cole H. Turner
	 */

	private static final long serialVersionUID = 1L;
	private static final int resolution = Toolkit.getDefaultToolkit().getScreenResolution();
	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static final int defaultRes = 160;

	private static Font dataFont = new Font(Font.DIALOG_INPUT, Font.BOLD, resolution / 4);

	private int max_i, res, dm;
	private boolean boolMax, boolRes, load, loaded, color, alpha;
	private Rectangle rpBounds;

	public MBGui(){
		super("Mandelbrot");
		Point screenCenter = new Point(screenSize.width / 2, screenSize.height / 2);
		max_i = 50;
		dm = 25;
		res = defaultRes;
		boolMax = false;
		boolRes = true;
		loaded = false;
		load = true;
		color = false;
		alpha = false;
		Container pane = new JPanel(){
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics gr){
				Graphics2D g = (Graphics2D) gr;
				g.setColor(Color.black);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setFont(dataFont);
				g.setColor(Color.white);
				int h = g.getFontMetrics().getHeight();
				g.drawString("Mandelbrot Set", h, h);
				String s = "resolution = " + res + ":1";
				g.drawString(s, h, 2 * h);
				if(boolRes){
					g.drawString(">", h/4, 2 * h);
				}
				s = "max_iterations = " + max_i;
				g.drawString(s, h, 3 * h);
				if(boolMax){
					g.drawString(">", h / 4, 3 * h);
				}
				if(!loaded && !load){
					g.drawString("*", h/4, h);
				}
				s = "";
				if(color) s += "RGB ";
				else if(alpha) s += "ALPHA";
				g.drawString(s, h, 4 * h);
				int scale = getWidth() / 5;
				rpBounds = new Rectangle(0, 0, 2 * (getWidth() / 3 - (int)scale), getHeight());
				g.translate(getWidth() * 2 / 3, getHeight() / 2);
				if(load){
					g.scale(1, -1);
					for(int x = -2; x < 2; x++){
						for(int y = -2; y < 2; y++){
							for(int cx = 0; cx < res; cx++){
								for(int cy = 0; cy < res; cy++){
									double cRe, cIm, zRe, zIm, z2Re, z2Im, r;
									cRe = x + (double)cx / res;
									cIm = y + (double)cy / res;
									zRe = 0;
									zIm = 0;
									int i;
									boolean contains = true;
									for(i = 0; i < max_i; i++){
										z2Re = zRe * zRe - zIm * zIm;
										z2Im = 2 * zRe * zIm;
										zRe = z2Re + cRe;
										zIm = z2Im + cIm;
										if(Math.sqrt(zRe * zRe + zIm * zIm) > 2){
											contains = false;
											break;
										}
									}
									r = scale / res + 0.5;
									int[] rgb = new int[3];
									int a = 255;
									if(color){
										double val = (double) (i * 4.0 / max_i);
										int n;
										for(int m = 0; m < 3; m++){
											n = Math.floorMod(((int)val) - m, 4);
											rgb[m] = (int)Math.abs(Math.sin(Math.PI * val / 2) * 255) * (int)(1 - n / 2);
										}
									} else {
										if(alpha) a = i == 0 ? 0 : 255 * i / max_i;
										for(int m = 0; m < 3; m++)
											rgb[m] = 255;
									}
									if(contains || color || alpha){
										g.setColor(new Color(rgb[0], rgb[1], rgb[2], a));
										g.fillRect((int)(scale * cRe - r/2), (int)(scale * cIm - r/2), (int)r, (int)r);
									}
								}
							}
						}
					}
					g.setColor(Color.darkGray);
					for(int x = -2; x <= 1; x++) 
						g.drawLine(x * scale, h / 2, x * scale, -h / 2);
					for(int y = -1; y <= 1; y++)
						g.drawLine(h / 2, y * scale, -h / 2 , y * scale);
					load = false;
					loaded = true;
					MBGui.this.update();
				}
			}
		};

		this.setSize(screenSize);
		this.setContentPane(pane);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addMouseListener(this);
		this.addKeyListener(this);
		this.setLocation(screenCenter.x - this.getWidth() / 2, screenCenter.y - this.getHeight() / 2);
		this.setUndecorated(true);
		this.setVisible(true);
	}

	public boolean contains(double x, double y){
		return true;
	}

	public void update(){
		this.repaint(rpBounds.x, rpBounds.y, rpBounds.width, rpBounds.height);
	}

	public void actionPerformed(ActionEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_R :
			boolRes = !boolRes;
			boolMax = false;
			break;
		case KeyEvent.VK_M :
			boolMax =!boolMax;
			boolRes = false;
			break;
		case KeyEvent.VK_UP :
			if(boolRes) res = res == 16 ? 20 : res == 640 ? 640 : res * 2;
			else if(boolMax) max_i += max_i == 500 ? 0 : max_i < dm ? 5 : dm;
			else return;
			loaded = false;
			break;
		case KeyEvent.VK_DOWN :
			if(boolRes) res = res == 20 ? 16 : res == 4 ? 4 : res / 2;
			else if(boolMax) max_i -= max_i == 5 ? 0 : max_i <= dm ? 5 : dm;
			else return;
			loaded = false;
			break;
		case KeyEvent.VK_C :
			color = !color;
			alpha = false;
			loaded = false;
			break;
		case KeyEvent.VK_A :
			alpha = !alpha;
			color = false;
			loaded = false;
			break;
		case KeyEvent.VK_ENTER :
			load = true;
			repaint();
			return;
		case KeyEvent.VK_ESCAPE :
			System.exit(0);
		}
		update();
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new MBGui();
			}
		});
	}
}

package hardcoded.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.*;

import hardcoded.analyser.RustDraw;

/**
 * Used for roughly selecting the area of the painting
 * 
 * @author HardCoded
 */
public class RustWindow extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 6257883605419238535L;
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		
		JFrame frame = new JFrame("Draggable Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.add(new RustWindow());
		frame.pack();
		frame.setBackground(new Color(0,0,0,0));
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		frame.repaint();
		
		Thread thread = new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(50);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				
				frame.repaint();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	protected RustSettings settings = new RustSettings();
	protected RustFileChooser fileChooser;
	protected RustCanvas canvas;
	protected Dimension size;
	private JPanel menu;
	
	private boolean minified = false;
	private boolean show_canvas = true;
	private boolean drawing_test;

	public RustWindow() {
		size = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			
		}
		
		setOpaque(false);
		setLayout(null);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		menu = new JPanel();
		menu.setBounds(size.width - 260, 20, 220, 120 + 24);
		menu.setBackground(new Color(120, 120, 120));
		{
			menu.setLayout(null);
			JLabel label = new JLabel("Menu");
			label.setForeground(Color.white);
			label.setBounds(6, 0, 200, 24);
			menu.add(label);
			
			fileChooser = new RustFileChooser(this);
			
			int y = 0;
			final JButton btn_add_texture = new JButton("Set texture");
			btn_add_texture.setFocusable(false);
			btn_add_texture.setForeground(Color.black);
			btn_add_texture.setBackground(Color.gray);
			btn_add_texture.setBounds(4, y += 24, 212, 20);
			btn_add_texture.addActionListener((event) -> {
				int result = fileChooser.showOpenDialog();
				if(result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					
					try {
						canvas.widget.setImage(ImageIO.read(file));
					} catch(Exception e) {
						
					}
				}
			});
			menu.add(btn_add_texture);
			
			final JButton btn_reset = new JButton("Reset Texture Area");
			btn_reset.setFocusable(false);
			btn_reset.setForeground(Color.black);
			btn_reset.setBackground(Color.gray);
			btn_reset.setBounds(4, y += 24, 212, 20);
			btn_reset.addActionListener((event) -> {
				canvas.widget.y_min = size.height / 2 - 50;
				canvas.widget.y_max = size.height / 2 + 50;
				canvas.widget.x_min = size.width / 2 - 50;
				canvas.widget.x_max = size.width / 2 + 50;
				canvas.widget.angle = 0;
				canvas.widget.updatePoints();
			});
			menu.add(btn_reset);
			
			final JButton btn_start = new JButton("Start Drawing");
			btn_start.setFocusable(false);
			btn_start.setForeground(Color.black);
			btn_start.setBackground(Color.gray);
			btn_start.setBounds(4, y += 24, 212, 20);
			btn_start.addActionListener((event) -> {
				{
					BufferedImage raster = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = raster.createGraphics();
					g.clip(canvas.area.getShape());
					canvas.widget.draw(g);
					g.dispose();
					canvas.rasterImage = raster;
				}
				
				if(drawing_test) return;
				drawing_test = true;
				repaint();
				
				Thread thread = new Thread(() -> {
					try {
						Thread.sleep(100);
						RustDraw draw = new RustDraw();
						draw.test(getParentFrame().getBounds(), canvas.rasterImage);
						//RustGUIAnalyser test = new RustGUIAnalyser(new Robot());
						
						Thread.sleep(1000);
					} catch(Exception e) {
						e.printStackTrace();
					}
					
					drawing_test = false;
					
					getParentFrame().requestFocus();
				});
				thread.start();
			});
			menu.add(btn_start);
			
			final JButton btn_show_canvas = new JButton("Hide/Show Canvas");
			btn_show_canvas.setFocusable(false);
			btn_show_canvas.setForeground(Color.black);
			btn_show_canvas.setBackground(Color.gray);
			btn_show_canvas.setBounds(4, y += 24, 212, 20);
			btn_show_canvas.addActionListener((event) -> {
				show_canvas = !show_canvas;
				repaint();
			});
			menu.add(btn_show_canvas);
			
			final JButton btn_about = new JButton("About");
			btn_about.setFocusable(false);
			btn_about.setForeground(Color.black);
			btn_about.setBackground(Color.gray);
			btn_about.setBounds(4, y += 24, 212, 20);
			btn_about.addActionListener((event) -> {
				// TODO: About popup.
				// Fix so that the popup does not hide behind this window
			});
			menu.add(btn_about);
		}
		add(menu);
		
		canvas = new RustCanvas(this);
	}
	
	private JFrame cached_parent;
	private JFrame getParentFrame() {
		return Objects.requireNonNull(cached_parent, "Cached parent window was null");
	}
	
	public Dimension getPreferredSize() {
		cached_parent = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this);
		if(cached_parent == null) {
			throw new NullPointerException("Failed to cache the parent of the draggable window");
		}
		
		return size;
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		
		if(minified) {
			g2d.setColor(new Color(30, 30, 30, 100));
			g2d.fillRect(0, 0, getWidth(), getHeight());
		} else {
			g2d.setColor(new Color(0, 0, 0, 64));
			if(drawing_test) {
				g2d.fillRect(0, 0, getWidth() - 150, getHeight());
			} else {
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		}
		
		paintTaskBar(g2d);
		
		if(!minified) {
			canvas.draw(g);
		}
		
		if(drawing_test) {
			Rectangle2D rect = canvas.area.toRectangle2D();
			g2d.clearRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
		}
		
		if(!show_canvas && !minified) {
			g2d.clearRect(0, 20, size.width, size.height - 20);
		}
		
		if(minified) {
			g2d.setColor(Color.black);
			g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			g2d.drawLine(0, 20, getWidth(), 20);
		}
		super.paint(g);
	}
	
	private void paintTaskBar(Graphics2D g) {
		g.setColor(new Color(66, 66, 66));
		g.fillRect(0, 0, getWidth(), 20);
		
		{
			g.setColor(Color.white);
			Rectangle rect = new Rectangle(0, 0, 80, 20);
			drawCenteredString(g, "Rust Picasso", rect);
		}
		
		Stroke stroke = g.getStroke();
		
		{ // Exit button
			g.setColor(new Color(200, 0, 0));
			g.fillRect(getWidth() - 20, 0, 20, 20);
			g.setColor(Color.white);
			g.setStroke(new BasicStroke(2));
			g.drawLine(getWidth() - 15, 5, getWidth() - 6, 14);
			g.drawLine(getWidth() - 15, 14, getWidth() - 6, 5);
		}
		
		{ // Minimize button
			g.setColor(new Color(127, 80, 80));
			g.fillRect(getWidth() - 40, 0, 20, 20);
			g.setColor(Color.white);
			g.setStroke(new BasicStroke(1.5f));
			if(minified) {
				g.drawRect(getWidth() - 36, 4, 11, 11);
			} else {
				g.drawLine(getWidth() - 36, 10, getWidth() - 25, 10);
			}
		}
		
		{ // Menu dropdown
			g.setColor(new Color(80, 80, 127));
			g.fillRect(getWidth() - 100, 0, 60, 20);
			g.setColor(Color.white);
			g.setStroke(new BasicStroke(1.5f));
			g.drawLine(getWidth() - 76, 6, getWidth() - 64, 6);
			g.drawLine(getWidth() - 76, 10, getWidth() - 64, 10);
			g.drawLine(getWidth() - 76, 14, getWidth() - 64, 14);
		}
		
		g.setStroke(stroke);
	}
	
	private void drawCenteredString(Graphics2D g, String text, Rectangle rect) {
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		g.drawString(text, x, y);
	}
	
	private boolean isDraggingWindow;
	private int dragOffsetX;
	private int dragOffsetY;
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.getY() < 20) {
				if(e.getX() < getWidth() - 100) {
					isDraggingWindow = true;
					
					if(minified) {
						Point loc = getParentFrame().getLocation();
						dragOffsetX = e.getXOnScreen() - loc.x;
						dragOffsetY = e.getYOnScreen() - loc.y;
					}
				} else if(e.getX() < getWidth() - 40) {
					menu.setVisible(!menu.isVisible());
				} else if(e.getX() < getWidth() - 20) {
					// Minimize
					JFrame frame = getParentFrame();
					Rectangle rect = Utils.getScreenSizeForPosition(e.getLocationOnScreen());
					
					minified = !minified;
					if(minified) {
						frame.setBounds(
							rect.x + (rect.width - 540) / 2,
							rect.y + (rect.height - 360) / 2,
							540,
							360
						);
						size.width = 540;
						size.height = 360;
						menu.setLocation(size.width - 260, 20);
					} else {
						if(rect != null && !rect.equals(frame.getBounds())) {
							frame.setBounds(rect);
							size = rect.getSize();
							canvas.area.setRectangle(new Rectangle(8, 28, size.width - 8, size.height - 8));
							menu.setLocation(size.width - 260, 20);
						}
					}
				} else {
					getParentFrame().dispose();
				}
			}
		}
		
		if(!minified) canvas.mousePressed(e);
	}
	
	public void mouseReleased(MouseEvent e) {
		isDraggingWindow = false;
		if(!minified) canvas.mouseReleased(e);
		repaint();
	}
	
	public void mouseMoved(MouseEvent e) {
		if(e.getY() < 20) {
			if(e.getX() < getWidth() - 100) {
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			} else {
				setCursor(Cursor.getDefaultCursor());
			}
		} else {
			setCursor(Cursor.getDefaultCursor());
		}
		
		if(!minified) canvas.mouseMoved(e);
	}
	
	public void mouseDragged(MouseEvent e) {
		if(isDraggingWindow) {
			JFrame frame = getParentFrame();
			Rectangle rect = Utils.getScreenSizeForPosition(e.getLocationOnScreen());
			
			if(minified) {
				int x = e.getXOnScreen() - dragOffsetX;
				int y = e.getYOnScreen() - dragOffsetY;
				
				if(y < 0) y = 0;
				if(y > rect.height - 20) y = rect.height - 20;
				frame.setLocation(x, y);
			} else {
				if(rect != null && !rect.equals(frame.getBounds())) {
					frame.setBounds(rect);
					size = rect.getSize();
					canvas.area.setRectangle(new Rectangle(8, 28, size.width - 8, size.height - 8));
					menu.setLocation(size.width - 260, 20);
				}
			}
		} else {
			if(!minified) canvas.mouseDragged(e);
		}
	}
	
	// Unused
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}

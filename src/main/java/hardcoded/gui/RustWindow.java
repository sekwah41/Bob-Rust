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
import hardcoded.main.Area;
import hardcoded.main.DraggableWindow;
import hardcoded.math.Point2f;
import hardcoded.widget.ImageStretch;

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
	private Area area = new Area();
	private BufferedImage image;
	private Dimension size;
	private JPanel menu;
	
	private BufferedImage rasterImage;
	private ImageStretch widget;
	private boolean drawing_test;
	
	public RustWindow() {
		size = Toolkit.getDefaultToolkit().getScreenSize();
		init();
		
		setOpaque(false);
		setLayout(null);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		area.getPoint(0).set((size.width / 2) - 100, (size.height / 2) - 100);
		area.getPoint(1).set((size.width / 2) + 100, (size.height / 2) - 100);
		area.getPoint(2).set((size.width / 2) + 100, (size.height / 2) + 100);
		area.getPoint(3).set((size.width / 2) - 100, (size.height / 2) + 100);
		area.setRectangle(new Rectangle(8, 8, size.width - 8, size.height - 8));
		
		menu = new JPanel();
		menu.setBounds(size.width - 240, 20, 220, 120);
		menu.setBackground(new Color(120, 120, 120));
		{
			menu.setLayout(null);
			JLabel label = new JLabel("Image path");
			label.setForeground(Color.white);
			label.setBounds(6, 0, 200, 24);
			menu.add(label);
			
			fileChooser = new RustFileChooser(this);
			
			final JButton btn_add_texture = new JButton("Set texture");
			btn_add_texture.setFocusable(false);
			btn_add_texture.setForeground(Color.black);
			btn_add_texture.setBackground(Color.gray);
			btn_add_texture.setBounds(4, 24, 212, 20);
			btn_add_texture.addActionListener((event) -> {
				int result = fileChooser.showOpenDialog();
				if(result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					
					try {
						widget.setImage(ImageIO.read(file));
					} catch(Exception e) {
						
					}
				}
			});
			menu.add(btn_add_texture);
			
			final JButton btn_raster = new JButton("Raster Image");
			btn_raster.setFocusable(false);
			btn_raster.setForeground(Color.black);
			btn_raster.setBackground(Color.gray);
			btn_raster.setBounds(4, 48, 212, 20);
			btn_raster.addActionListener((event) -> {
				BufferedImage raster = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = raster.createGraphics();
				g.clip(area.getShape());
				widget.draw(g);
				g.dispose();
				rasterImage = raster;
			});
			menu.add(btn_raster);
			
			final JButton btn_reset = new JButton("Reset Texture Area");
			btn_reset.setFocusable(false);
			btn_reset.setForeground(Color.black);
			btn_reset.setBackground(Color.gray);
			btn_reset.setBounds(4, 72, 212, 20);
			btn_reset.addActionListener((event) -> {
				widget.y_min = size.height / 2 - 50;
				widget.y_max = size.height / 2 + 50;
				widget.x_min = size.width / 2 - 50;
				widget.x_max = size.width / 2 + 50;
				widget.angle = 0;
				widget.updatePoints();
			});
			menu.add(btn_reset);
			
			final JButton btn_start = new JButton("Start Drawing");
			btn_start.setFocusable(false);
			btn_start.setForeground(Color.black);
			btn_start.setBackground(Color.gray);
			btn_start.setBounds(4, 96, 212, 20);
			btn_start.addActionListener((event) -> {
				if(drawing_test) return;
				System.out.println("Start Drawing");
				
				drawing_test = true;
				repaint();
				
				Thread thread = new Thread(() -> {
					try {
						Thread.sleep(100);
						RustDraw draw = new RustDraw();
						draw.test(getParentFrame().getBounds(), rasterImage);
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
			
			// TODO: Add chroma key and transparent pixel options.
		}
		add(menu);
	}
	
	private void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			
		}
		
		try {
			image = ImageIO.read(DraggableWindow.class.getResourceAsStream("/checkers.png"));
		} catch(Exception e) {
			
		}
		
		if(image == null) {
			// If the image fails to load
			image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.magenta);
			g.fillRect(0, 0, 8, 8);
			g.fillRect(8, 8, 8, 8);
		}
		
		widget = new ImageStretch(image);
		widget.y_min = size.height / 2 - 50;
		widget.y_max = size.height / 2 + 50;
		widget.x_min = size.width / 2 - 50;
		widget.x_max = size.width / 2 + 50;
		widget.updatePoints();
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
		
		g2d.setColor(new Color(0, 0, 0, 64));
		if(drawing_test) {
			g2d.fillRect(0, 0, getWidth() - 150, getHeight());
		} else {
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}
		
		BufferedImage bi = rasterImage;
		if(bi != null) {
			g2d.drawImage(bi, 0, 0, null);
		}
		paintOverlay(g2d);
		
		if(drawing_test) {
			Rectangle2D rect = area.toRectangle2D();
			g2d.clearRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
		}
		g2d.dispose();
		
		super.paint(g);
	}
	
	private void paintOverlay(Graphics2D g) {
		paintTaskBar(g);
		paintPoints(g);
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
			g.drawLine(getWidth() - 14, 6, getWidth() - 6, 14);
			g.drawLine(getWidth() - 6, 6, getWidth() - 14, 14);
		}
		
		{ // Menu dropdown
			g.setColor(new Color(80, 80, 127));
			g.fillRect(getWidth() - 80, 0, 60, 20);
			g.setColor(Color.white);
			g.setStroke(new BasicStroke(1.5f));
			g.drawLine(getWidth() - 56, 6, getWidth() - 44, 6);
			g.drawLine(getWidth() - 56, 10, getWidth() - 44, 10);
			g.drawLine(getWidth() - 56, 14, getWidth() - 44, 14);
		}
		
		g.setStroke(stroke);
	}
	
	private void drawCenteredString(Graphics2D g, String text, Rectangle rect) {
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		g.drawString(text, x, y);
	}
	
	private int hover_point = -1;
	private void paintPoints(Graphics2D g) {
		{
			Graphics2D copy = (Graphics2D)g.create();
			Shape shape = area.getShape();
			copy.setClip(shape);
			widget.draw(copy);
			
			// sun.java2d.SunGraphics2D
			{
				Rectangle screen = getParentFrame().getBounds();
				java.awt.geom.Area inverse = new java.awt.geom.Area(new Rectangle2D.Float(0, 0, screen.width, screen.height));
				inverse.subtract(new java.awt.geom.Area(shape));
				copy.setClip(inverse);
				
				Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC, 0.3f);
				copy.setComposite(comp);
				widget.draw(copy);
			}
			copy.dispose();
			
			widget.drawOverlay(g);
		}
		
		g.setColor(Color.white);
		for(int i = 0; i < 4; i++) {
			Point2f a = area.getPoint(i);
			Point2f b = area.getPoint((i + 1) % 4);
			
			int x1 = (int)a.x;
			int y1 = (int)a.y;
			int x2 = (int)b.x;
			int y2 = (int)b.y;
			g.drawLine(x1, y1, x2, y2);
		}
		
		{
			g.setColor(Color.cyan);
			for(int i = 0; i < 4; i++) {
				int oval_size = 12;
				if(hover_point == i) {
					oval_size = 16;
				}
				
				Point2f a = area.getPoint(i);
				
				int x1 = (int)a.x;
				int y1 = (int)a.y;
				
				g.fillOval(x1 - oval_size / 2, y1 - oval_size / 2, oval_size, oval_size);
			}
		}
	}
	
	private boolean isDraggingWindow;
	private boolean isDraggingPoint;
	private int draggedPoint;
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.getY() < 20) {
				if(e.getX() < getWidth() - 80) {
					isDraggingWindow = true;
				} else if(e.getX() < getWidth() - 20) {
					menu.setVisible(!menu.isVisible());
				} else {
					getParentFrame().dispose();
				}
			}
		}
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			int index = area.getPointIndex(e.getPoint(), 12);
			if(index != -1) {
				isDraggingPoint = true;
				draggedPoint = index;
			}
		}
		
		widget.onMousePressed(e);
	}
	
	public void mouseReleased(MouseEvent e) {
		if(isDraggingPoint) {
			area.update();
		}
		
		isDraggingWindow = false;
		isDraggingPoint = false;
		
		widget.onMouseRelease(e);
		repaint();
	}
	
	public void mouseMoved(MouseEvent e) {
		if(e.getY() < 20) {
			if(e.getX() < getWidth() - 80) {
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			} else {
				setCursor(Cursor.getDefaultCursor());
			}
		} else {
			setCursor(Cursor.getDefaultCursor());
		}
		
		{
			int hover = -1;
			Point mouse = e.getPoint();
			for(int i = 0; i < 4; i++) {
				Point2f p = area.getPoint(i);
				
				if(mouse.distance(p.x, p.y) < 12) {
					hover = i;
				}
			}
			
			hover_point = hover;
		}
		
		widget.onMouseMove(e);
	}
	
	public void mouseDragged(MouseEvent e) {
		if(isDraggingWindow) {
			JFrame frame = getParentFrame();
			Rectangle rect = Utils.getScreenSizeForPosition(e.getLocationOnScreen());
			
			if(rect != null && !rect.equals(frame.getBounds())) {
				frame.setBounds(rect);
				size = rect.getSize();
				area.setRectangle(new Rectangle(8, 8, size.width - 8, size.height - 8));
				menu.setLocation(size.width - 240, 20);
			}
		}
		
		if(isDraggingPoint) {
			int index = draggedPoint;
			if(index != -1) {
				Point2f p = area.getPoint(index);
				
				int x = e.getX();
				int y = e.getY();
				if(x < 8) x = 8;
				if(x > size.width - 8) x = size.width - 8;
				if(y < 28) y = 28;
				if(y > size.height - 8) y = size.height - 8;
				
				p.set(x, y);
				area.update();
				for(int i = 0; i < 4; i++) {
					if(p == area.getPoint(i)) draggedPoint = i;
				}
				
				repaint();
			}
		} else {
			widget.onMouseDrag(e);
			repaint();
		}
	}
	
	// Unused
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}

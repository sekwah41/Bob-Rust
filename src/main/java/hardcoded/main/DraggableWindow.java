package hardcoded.main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import hardcoded.analyser.RustDraw;
import hardcoded.math.Point2f;
import hardcoded.widget.ImageStretch;

/**
 * Used for roughly selecting the area of the painting
 * 
 * @author HardCoded
 */
public class DraggableWindow extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 4378794890442241888L;
	
	// TODO: Use javafx for the more system looking file choosers. (Better UX)
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		
		JFrame frame = new JFrame("Draggable Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.add(new DraggableWindow());
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
	
	private static final String LAST_FILE_PATH = "lastFilePath";
	
	private Area area = new Area();
	private Dimension size;
	private BufferedImage image;
	private JFileChooser fileChooser;
	private Properties settings;
	private JPanel menu;
	
	private BufferedImage IMAGE_RASTER;
	private ImageStretch widget;
	private boolean drawing_test;
	// private List<ImageWidget> widgets = new ArrayList<>();
	//private MenuWidgetTree widgetTree;
	private ConcurrentLinkedQueue<Callable<?>> after_render;
	
	public DraggableWindow() {
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
			
			fileChooser = new JFileChooser(new File(settings.getProperty(LAST_FILE_PATH)));
			fileChooser.setMultiSelectionEnabled(false);
			
			final JButton btn_add_texture = new JButton("Set texture");
			btn_add_texture.setFocusable(false);
			btn_add_texture.setForeground(Color.black);
			btn_add_texture.setBackground(Color.gray);
			btn_add_texture.setBounds(4, 24, 212, 20);
			btn_add_texture.addActionListener((event) -> {
				// Does not seem to work
//				Rectangle rect = getParentFrame().getBounds();
//				fileChooser.setLocation(
//					rect.x + (rect.width / 2) - (fileChooser.getWidth() / 2),
//					rect.y + (rect.height / 2) - (fileChooser.getHeight() / 2)
//				);
				
				fileChooser.setCurrentDirectory(new File(settings.getProperty(LAST_FILE_PATH)));
				int result = fileChooser.showOpenDialog(DraggableWindow.this);
				if(result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					
					try {
						image = ImageIO.read(file);
						/*
						if(widgets.isEmpty()) {
							ImageStretch widget = new ImageStretch(image);
							widget.y_min = size.height / 2 - 50;
							widget.y_max = size.height / 2 + 50;
							widget.x_min = size.width / 2 - 50;
							widget.x_max = size.width / 2 + 50;
							widget.updatePoints();
							widgets.add(widget);
						} else {
							ImageWidget widget = widgets.get(0);
							widget.setImage(image);
						}
						*/
						
						widget.setImage(image);
					} catch(Exception e) {
						
					}
				}
				
				File dir = fileChooser.getCurrentDirectory();
				if(dir != null) {
					settings.setProperty(LAST_FILE_PATH, dir.toString());
					saveSettings();
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
				
				//if(!widgets.isEmpty()) {
					Point2f a = area.getPoint(0);
					Point2f b = area.getPoint(1);
					Point2f c = area.getPoint(2);
					Point2f d = area.getPoint(3);
					
					Path2D path = new Path2D.Float();
					path.moveTo(a.x, a.y);
					path.lineTo(b.x, b.y);
					path.lineTo(c.x, c.y);
					path.lineTo(d.x, d.y);
					path.closePath();
					g.clip(path);
					
					//ImageWidget widget = widgets.get(0);
					widget.draw(g);
					
					IMAGE_RASTER = raster;
				//}
			});
			menu.add(btn_raster);
			
			final JButton btn_reset = new JButton("Reset Texture Area");
			btn_reset.setFocusable(false);
			btn_reset.setForeground(Color.black);
			btn_reset.setBackground(Color.gray);
			btn_reset.setBounds(4, 72, 212, 20);
			btn_reset.addActionListener((event) -> {
				widget = new ImageStretch(image);
				widget.y_min = size.height / 2 - 50;
				widget.y_max = size.height / 2 + 50;
				widget.x_min = size.width / 2 - 50;
				widget.x_max = size.width / 2 + 50;
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
						draw.test(getParentFrame().getBounds(), IMAGE_RASTER);
						//RustGUIAnalyser test = new RustGUIAnalyser(new Robot());
						
						Thread.sleep(1000);
					} catch(Exception e) {
						e.printStackTrace();
					}
//					DrawImage draw = new DrawImage();
//					draw.test(IMAGE_RASTER);
					drawing_test = false;
					
					getParentFrame().requestFocus();
				});
				thread.start();
			});
			menu.add(btn_start);
			
//			widgetTree = new MenuWidgetTree(widgets);
//			widgetTree.setFocusable(false);
//			widgetTree.setBounds(4, 50, 212, 400);
//			widgetTree.setOpaque(false);
//			menu.add(widgetTree);
			
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
		
		loadSettings();
	}
	
	private void loadSettings() {
		File parent = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(parent, ".rust.picasso.properties");
		
		settings = new Properties();
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch(Exception e) {
				
			}
		} else {
			try(FileInputStream stream = new FileInputStream(file)) {
				settings.load(stream);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		settings.putIfAbsent(LAST_FILE_PATH, FileSystemView.getFileSystemView().getHomeDirectory().toString());
	}
	
	private void saveSettings() {
		File parent = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(parent, ".rust.picasso.properties");
		
		try(FileOutputStream stream = new FileOutputStream(file)) {
			settings.store(stream, "");
		} catch(Exception e) {
			
		}
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
		BufferedImage bi = IMAGE_RASTER;
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
	
	// TODO: Maybe a smooth animation when lines change from point to point?
	private int hover_point = -1;
	private void paintPoints(Graphics2D g) {
		{
			//for(ImageWidget widget : widgets) widget.draw(g);
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
			
//			for(int i = 0; i < 4; i++) {
//				Point2f a = area.getPoint(i);
//				
//				Rectangle rect = new Rectangle((int)a.x, (int)a.y, (int)(center.x - a.x), (int)(center.y - a.y));
//				drawCenteredString(g, "Angle: " + String.format("%.3f", a.getAngle(center)), rect);
//			}
		}
	}
	
	@SuppressWarnings("unused")
	private void drawCenteredString(Graphics2D g, String text, Rectangle rect) {
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		g.drawString(text, x, y);
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
		// for(int i = widgets.size() - 1; i >= 0 && !e.isConsumed(); i--) widgets.get(i).onMousePressed(e);
		// if(e.isConsumed()) widgetTree.update();
	}
	
	public void mouseReleased(MouseEvent e) {
		if(isDraggingPoint) {
			area.update();
		}
		
		isDraggingWindow = false;
		isDraggingPoint = false;
		
		widget.onMouseRelease(e);
		// for(int i = widgets.size() - 1; i >= 0 && !e.isConsumed(); i--) widgets.get(i).onMouseRelease(e);
		// if(e.isConsumed()) widgetTree.update();
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
		// for(int i = widgets.size() - 1; i >= 0 && !e.isConsumed(); i--) widgets.get(i).onMouseMove(e);
		// if(e.isConsumed()) widgetTree.update();
	}
	
	public void mouseDragged(MouseEvent e) {
		if(isDraggingWindow) {
			JFrame frame = getParentFrame();
			Rectangle rect = getScreenSizeForPosition(e.getLocationOnScreen());
			
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
			// for(int i = widgets.size() - 1; i >= 0 && !e.isConsumed(); i--) widgets.get(i).onMouseDrag(e);
			// if(e.isConsumed()) widgetTree.update();
			repaint();
		}
	}
	
	private Rectangle getScreenSizeForPosition(Point point) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for(GraphicsDevice device : gs) {
			GraphicsConfiguration[] gc = device.getConfigurations();
			for(GraphicsConfiguration config : gc) {
				Rectangle bounds = config.getBounds();
				
				if(bounds.contains(point)) {
					return bounds;
				}
			}
		}
		
		return null;
	}
	
	
	// Unused
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
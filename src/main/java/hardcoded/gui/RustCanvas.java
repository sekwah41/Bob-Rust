package hardcoded.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import hardcoded.main.Area;
import hardcoded.math.Point2f;
import hardcoded.widget.ImageStretch;

public class RustCanvas {
	private final RustWindow window;
	protected Area area = new Area();
	protected BufferedImage image;
	protected BufferedImage rasterImage;
	protected ImageStretch widget;
	
	public RustCanvas(RustWindow window) {
		this.window = window;
		init();
	}
	
	private void init() {
		try {
			image = ImageIO.read(RustCanvas.class.getResourceAsStream("/checkers.png"));
		} catch(Exception e) {
			
		}
		
		area.getPoint(0).set((window.size.width / 2) - 100, (window.size.height / 2) - 100);
		area.getPoint(1).set((window.size.width / 2) + 100, (window.size.height / 2) - 100);
		area.getPoint(2).set((window.size.width / 2) + 100, (window.size.height / 2) + 100);
		area.getPoint(3).set((window.size.width / 2) - 100, (window.size.height / 2) + 100);
		area.setRectangle(new Rectangle(8, 8, window.size.width - 8, window.size.height - 8));
		
		if(image == null) {
			// If the image fails to load
			image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.magenta);
			g.fillRect(0, 0, 8, 8);
			g.fillRect(8, 8, 8, 8);
		}
		
		widget = new ImageStretch(image);
		widget.y_min = window.size.height / 2 - 50;
		widget.y_max = window.size.height / 2 + 50;
		widget.x_min = window.size.width / 2 - 50;
		widget.x_max = window.size.width / 2 + 50;
		widget.updatePoints();
	}
	
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D)g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		
		paintPoints(g2d);
	}
	
//	private void drawCenteredString(Graphics2D g, String text, Rectangle rect) {
//		FontMetrics metrics = g.getFontMetrics(g.getFont());
//		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
//		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
//		g.drawString(text, x, y);
//	}
	
	private void paintPoints(Graphics2D g) {
		// TODO: Flickers when menu is hidden
		{
			Graphics2D copy = (Graphics2D)g.create();
			Shape shape = area.getShape();
			copy.setClip(shape);
			widget.draw(copy);
			
			{
				copy.setClip(Utils.createInverse(window.size, shape));
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

	private boolean isDraggingPoint;
	private int hover_point = -1;
	private int draggedPoint;
	
	public void mousePressed(MouseEvent e) {
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
		
		isDraggingPoint = false;
		
		widget.onMouseRelease(e);
		repaint();
	}
	
	public void mouseMoved(MouseEvent e) {
		int hover = -1;
		Point mouse = e.getPoint();
		for(int i = 0; i < 4; i++) {
			Point2f p = area.getPoint(i);
			
			if(mouse.distance(p.x, p.y) < 12) {
				hover = i;
			}
		}
		
		hover_point = hover;
		widget.onMouseMove(e);
	}
	
	public void mouseDragged(MouseEvent e) {
		if(isDraggingPoint) {
			int index = draggedPoint;
			if(index != -1) {
				Point2f p = area.getPoint(index);
				
				p.set(e.getX(), e.getY());
				Rectangle rect = new Rectangle(8, 28, window.size.width - 8, window.size.height - 8);
				area.setRectangle(rect);
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
	
	public void repaint() {
		if(window != null) {
			window.repaint(20);
		}
	}
}

package hardcoded.widget;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import hardcoded.math.MathUtils;
import hardcoded.math.Point2f;

// TODO: Clean the code and make this look pretty
public class ImageStretch implements ImageWidget {
	private static final Color darkYellow = new Color(127, 127, 0);
	public float y_min, y_max;
	public float x_min, x_max;
	public float angle;
	
	private boolean isVisible = true;
	private BufferedImage image;
	
	// TODO: Do we need this?
	private Point2f[] dragPoints = new Point2f[4];
	
	public ImageStretch(BufferedImage def) {
		for(int i = 0; i < 4; i++) dragPoints[i] = new Point2f();
		
		y_min = 0;
		y_max = 100;
		x_min = 0;
		x_max = 100;
		
		updatePoints();
		this.image = def;
	}
	
	public void draw(Graphics2D g) {
		float ox = (x_max + x_min) / 2.0f;
		float oy = (y_max + y_min) / 2.0f;
		float angle = (float)Math.toRadians(this.angle);
		g.rotate(angle, ox, oy);
		
		int ix = (int)(x_min);
		int iw = (int)(x_max - x_min);
		int iy = (int)(y_min);
		int ih = (int)(y_max - y_min);
		
		BufferedImage bi = image;
		if(bi != null) {
			g.drawImage(bi, ix, iy, iw, ih, null);
		}
		
		g.rotate(-angle, ox, oy);
	}
	
	public void drawOverlay(Graphics2D g) {
		float ox = (x_max + x_min) / 2.0f;
		float oy = (y_max + y_min) / 2.0f;
		
		{ // Draw texture and border
			float angle = (float)Math.toRadians(this.angle);
			
			g.rotate(angle, ox, oy);
			
			int cx = (int)(x_max < x_min ? (x_max - x_min):(x_min));
			int cw = (int)(x_max < x_min ? (x_min - x_max):(x_max - x_min));
			int cy = (int)(y_max < y_min ? (y_max):(y_min));
			int ch = (int)(y_max < y_min ? (y_min - y_max):(y_max - y_min));
			
			g.setColor(new Color(0, 0, 0, isHovering ? 110:100));
			g.fillRect(cx, cy, cw, ch);
			
			g.setColor(darkYellow);
			g.drawRect(cx, cy, cw, ch);
			
			g.rotate(-angle, ox, oy);
		}
		
		g.setColor(Color.yellow);
		for(int i = 0; i < 4; i++) {
			if(i == 0) g.setColor(Color.red);
			if(i == 1) g.setColor(Color.green);
			if(i == 2) g.setColor(Color.blue);
			if(i == 3) g.setColor(Color.yellow);
			int size = 12;
			if(hoverIndex == i) {
				size = 16;
			}
			
			Point2f p = dragPoints[i];
			int px = (int)(p.x - size / 2.0f);
			int py = (int)(p.y - size / 2.0f);
			
			g.fillOval(px, py, size, size);
		}
		
		g.setColor(Color.white);
		for(int i = 0; i < 4; i++) {
			int size = 12;
			Point2f l = dragPoints[(i + 1) % 4];
			Point2f p = dragPoints[i];
			
			float fpx = p.x + l.x - ox;
			float fpy = p.y + l.y - oy;
			
			int px = (int)(fpx - size / 2.0f);
			int py = (int)(fpy - size / 2.0f);
			
			g.fillOval(px, py, size, size);
		}
		
		/* Center point */ {
			g.setColor(new Color(255, 255, 0, 127));
			int size = 12;
			if(hoverIndex == 4) {
				size = 16;
			}

			int px = (int)(ox - size / 2.0f);
			int py = (int)(oy - size / 2.0f);
			
			g.fillOval(px, py, size, size);
		}
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public boolean isInside(Point p) {
		float ox = (x_max + x_min) / 2.0f;
		float oy = (y_max + y_min) / 2.0f;
		
		Point2f a = MathUtils.rotate(new Point2f(p.x, p.y), -angle, ox, oy);
		
		float x_min = Math.min(this.x_min, this.x_max);
		float x_max = Math.max(this.x_min, this.x_max);
		float y_min = Math.min(this.y_min, this.y_max);
		float y_max = Math.max(this.y_min, this.y_max);
		
		return !(a.x < x_min || a.x > x_max || a.y < y_min || a.y > y_max);
	}
	
	public void updatePoints() {
		float C = (float)Math.cos(Math.toRadians(angle));
		float S = (float)Math.sin(Math.toRadians(angle));
		
		float ox = (x_max + x_min) / 2.0f;
		float oy = (y_max + y_min) / 2.0f;
		
		dragPoints[0].set((x_min + x_max) / 2.0f, y_min);
		dragPoints[1].set(x_min, (y_max + y_min) / 2.0f);
		dragPoints[2].set((x_min + x_max) / 2.0f, y_max);
		dragPoints[3].set(x_max, (y_max + y_min) / 2.0f);
		
		for(int i = 0; i < 4; i++) {
			Point2f p = dragPoints[i];
			float x = p.x - ox;
			float y = p.y - oy;
			
			float nx = x * C - y * S;
			float ny = x * S + y * C;
			
			p.x = nx + ox;
			p.y = ny + oy;
		}
	}
	
	// TODO: Do not instantiate any classes inside this method
	// TODO: This sometimes flickers with small movements.. Fix this
	private void setPoint(float x, float y, int index) {
		float ox = (x_max + x_min) / 2.0f;
		float oy = (y_max + y_min) / 2.0f;
		Point2f p = MathUtils.rotate(-angle, x, y, ox, oy);
		
		switch(index) {
			case 0: y_min = p.y; break;
			case 1: x_min = p.x; break;
			case 2: y_max = p.y; break;
			case 3: x_max = p.x; break;
			case 4: {
				float cx = x - ox;
				float cy = y - oy;
				
				x_min += cx;
				x_max += cx;
				y_min += cy;
				y_max += cy;
				updatePoints();
				return;
			}
		}
		
		float last_x = 0, last_y = 0; {
			Point2f last = dragPoints[(index + 2) % 4];
			last_x = last.x;
			last_y = last.y;
		}
		updatePoints();
		
		if(index < 4) {
			Point2f offset = dragPoints[(index + 2) % 4];
			
			float offset_x = last_x - offset.x;
			float offset_y = last_y - offset.y;
			
			y_min += offset_y;
			x_min += offset_x;
			y_max += offset_y;
			x_max += offset_x;
			updatePoints();
		}
	}
	
	public int getPointIndex(Point mouse, float distance) {
		for(int i = 0; i < 4; i++) {
			Point2f p = dragPoints[i];
			if(mouse.distance(p.x, p.y) < distance) return i;
		}
		
		float ox = (x_max + x_min) / 2.0f;
		float oy = (y_max + y_min) / 2.0f;
		if(mouse.distance(ox, oy) < distance) return 4;
		
		return -1;
	}
	
	public int getCornerPointIndex(Point mouse, float distance) {
		float ox = (x_max + x_min) / 2.0f;
		float oy = (y_max + y_min) / 2.0f;
		
		for(int i = 0; i < 4; i++) {
			Point2f l = dragPoints[(i + 1) % 4];
			Point2f p = dragPoints[i];
			if(mouse.distance(p.x + l.x - ox, p.y + l.y - oy) < distance) return i;
		}
		
		return -1;
	}
	
	private boolean isRotating;
	private boolean isDragging;
	private boolean isHovering;
	private int hoverIndex = -1;
	private int dragIndex = -1;
	private float startAngle;
	public void onMousePressed(MouseEvent e) {
		if(!isVisible()) return;
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			int index;
			
			index = getPointIndex(e.getPoint(), 12);
			if(index != -1) {
				isDragging = true;
				dragIndex = index;
				
				e.consume();
			}
			
			index = getCornerPointIndex(e.getPoint(), 12);
			if(index != -1) {
				isRotating = true;
				startAngle = angle;
				
				float ox = (x_max + x_min) / 2.0f;
				float oy = (y_max + y_min) / 2.0f;
				
				startAngle = angle - (float)Math.toDegrees(Math.atan2(e.getY() - oy, e.getX() - ox));
				
				e.consume();
			}
			
			if(isHovering) {
				e.consume();
			}
		}
	}
	
	public void onMouseRelease(MouseEvent e) {
		if(!isVisible()) return;
		
		isDragging = false;
		isRotating = false;
		startAngle = 0;
		dragIndex = -1;
	}
	
	public void onMouseMove(MouseEvent e) {
		if(!isVisible()) return;
		
		hoverIndex = getPointIndex(e.getPoint(), 12);
		isHovering = isInside(e.getPoint());
		if(isHovering) e.consume();
	}
	
	public void onMouseDrag(MouseEvent e) {
		if(!isVisible()) return;
		
		if(isDragging) {
			int index = dragIndex;
			if(index != -1) {
				setPoint(e.getX(), e.getY(), index);
			}
			
			e.consume();
		} else if(isRotating) {
			float ox = (x_max + x_min) / 2.0f;
			float oy = (y_max + y_min) / 2.0f;
			
			Point2f v = new Point2f(e.getX() - ox, e.getY() - oy).normalize();
			angle = startAngle + (float)Math.toDegrees(Math.atan2(v.y, v.x));
			if(!Float.isFinite(angle)) angle = 0;
			updatePoints();
			
			e.consume();
		}
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public void setVisible(boolean enable) {
		isVisible = enable;
		
		if(!enable) {
			isDragging = false;
			isRotating = false;
			isHovering = false;
			hoverIndex = -1;
			dragIndex = -1;
			startAngle = 0;
		}
	}
}

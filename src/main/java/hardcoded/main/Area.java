package hardcoded.main;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Comparator;

import hardcoded.math.Point2f;

public class Area {
	private Point2f[] points = new Point2f[4];
	
	public Area() {
		for(int i = 0; i < points.length; i++) {
			points[i] = new Point2f();
		}
	}
	
	/**
	 * Fix the location of the points
	 */
	private void process() {
		final Point2f center = getCenter();
		Arrays.sort(points, new Comparator<Point2f>() {
			public int compare(Point2f a, Point2f b) {
				return Float.compare(a.getAngle(center), b.getAngle(center));
			}
		});
	}
	
	public void update() {
		process();
	}
	
	public Point2f getPoint(int index) {
		return points[index];
	}
	
	public Point2f getCenter() {
		int length = points.length;
		float x = 0;
		float y = 0;
		
		for(int i = 0; i < length; i++) {
			x += points[i].x;
			y += points[i].y;
		}
		
		return new Point2f(x / (length + 0.0f), y / (length + 0.0f));
	}
	
	private float getArea(Point2f a, Point2f b, Point2f c) {
		return Math.abs((a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2.0f);
	}
	
	public float getArea() {
		return getArea(points[0], points[1], points[2]) + getArea(points[0], points[2], points[3]);
	}
	
	public void moveCenter(float x, float y) {
		Rectangle2D rect = toRectangle2D();
		
		float ox = x - (float)rect.getCenterX();
		float oy = y - (float)rect.getCenterY();
		for(Point2f p : points) {
			p.x += ox;
			p.y += oy;
		}
	}

	/**
	 * Restrain all points to be contained within this rectangle
	 * @param r the restraining rectangle
	 */
	public void setRectangle(Rectangle r) {
		for(Point2f p : points) {
			if(p.x < r.x) p.x = r.x;
			if(p.x > r.x + r.width) p.x = r.x + r.width;
			if(p.y < r.y) p.y = r.y;
			if(p.y > r.y + r.height) p.y = r.y + r.height;
		}
	}
	
	private float calculateSign(float x, float y, Point2f a, Point2f b) {
		return (x - b.x) * (a.y - b.y) - (a.x - b.x) * (y - b.y);
	}
	
	public void rotate(float degree) {
		Point2f center = getCenter();
		rotate(degree, center.x, center.y);
	}
	
	public void rotate(float degree, float origin_x, float origin_y) {
		float cosf = (float)Math.cos(Math.toRadians(degree));
		float sinf = (float)Math.sin(Math.toRadians(degree));
		
		for(int i = 0; i < 4; i++) {
			Point2f p = points[i];
			float x = p.x - origin_x;
			float y = p.y - origin_y;
			
			float nx_C = x * cosf - y * sinf;
			float ny_C = x * sinf + y * cosf;
			
			p.x = nx_C + origin_x;
			p.y = ny_C + origin_y;
		}
	}
	
	private boolean isInsideTriangle(Point2f a, Point2f b, Point2f c, float x, float y) {
		return calculateSign(x, y, a, b) < 0.0f
			&& calculateSign(x, y, b, c) < 0.0f
			&& calculateSign(x, y, c, a) < 0.0f;
	}
	
	public boolean isInside(float x, float y) {
		return isInsideTriangle(points[0], points[1], points[2], x, y)
			|| isInsideTriangle(points[0], points[2], points[3], x, y);
	}

	public Rectangle2D toRectangle2D() {
		Point2f a = getPoint(0);
		Point2f b = getPoint(1);
		Point2f c = getPoint(2);
		Point2f d = getPoint(3);
		
		float x_min = min(a.x, b.x, c.x, d.x);
		float x_max = max(a.x, b.x, c.x, d.x);
		float y_min = min(a.y, b.y, c.y, d.y);
		float y_max = max(a.y, b.y, c.y, d.y);
		
		return new Rectangle2D.Float(x_min, y_min, x_max - x_min, y_max - y_min);
	}
	
	public Shape getShape() {
		Path2D path = new Path2D.Float();
		Point2f a = getPoint(0);
		Point2f b = getPoint(1);
		Point2f c = getPoint(2);
		Point2f d = getPoint(3);
		path.moveTo(a.x, a.y);
		path.lineTo(b.x, b.y);
		path.lineTo(c.x, c.y);
		path.lineTo(d.x, d.y);
		path.closePath();
		return path;
	}
	
	public int getPointIndex(Point mouse, float distance) {
		for(int i = 0; i < 4; i++) {
			Point2f p = points[i];
			
			if(mouse.distance(p.x, p.y) < distance) return i;
		}
		
		return -1;
	}
	
	private static float min(float... array) {
		float result = Float.MAX_VALUE;
		for(float f : array) result = f < result ? f:result;
		return result;
	}
	
	private static float max(float... array) {
		float result = -Float.MAX_VALUE;
		for(float f : array) result = f > result ? f:result;
		return result;
	}
}

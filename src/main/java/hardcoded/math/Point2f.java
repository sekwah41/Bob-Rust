package hardcoded.math;

public class Point2f {
	public float x;
	public float y;
	
	public Point2f() {
		
	}
	
	public Point2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Point2f point) {
		set(point.x, point.y);
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getMagnitude() {
		return x * x + y * y;
	}
	
	public Point2f normalize() {
		float length = length();
		if(Float.isFinite(length)) {
			x /= length;
			y /= length;
		}
		
		return this;
	}
	
	/**
	 * @return the angle from (0, 1) counter clockwise
	 */
	public float getAngle() {
		float angle = ((float)Math.toDegrees(Math.atan2(y, x)));
		angle = (angle + 180) % 360;
		
		if(angle < 0) {
			angle += 360;
		}
		
		return angle;
	}
	
	public float getAngle(Point2f origin) {
		float angle = (float)Math.toDegrees(Math.atan2(y - origin.y, x - origin.x));
		angle = (angle + 180) % 360;
		
		if(angle < 0) {
			angle += 360;
		}
		
		return angle;
	}
	
	public float getDistance(Point2f point) {
		return getDistance(point.x, point.y);
	}
	
	public Point2f clone() {
		return new Point2f(x, y);
	}
	
	public float getDistance(float x, float y) {
		float dx = x - this.x;
		float dy = y - this.y;
		return (float)Math.sqrt(dx * dx + dy * dy);
	}
	
	public float length() {
		return (float)Math.sqrt(x * x + y * y);
	}
	
	public String toString() {
		return String.format("(%.7f, %.7f)", x, y);
	}
	
	public static Point2f middle(Point2f a, Point2f b) {
		return new Point2f((a.x + b.x) / 2.0f, (a.y + b.y) / 2.0f);
	}

	public static float distance(Point2f a, Point2f b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		return (float)Math.sqrt(dx * dx + dy * dy);
	}
}

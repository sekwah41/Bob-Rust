package hardcoded.math;

public final class MathUtils {
	
	public static float distanceToEdge(Point2f a, Point2f b, Point2f point) {
		Point2f v0 = closestPointOnEdge(a, b, point);
		float dot = dot(a, b, point);
		
		if(dot < 0) {
			return -Point2f.distance(v0, point);
		}
		
		return Point2f.distance(v0, point);
	}
	
	public static Point2f closestPointOnEdge(Point2f v0, Point2f v1, Point2f point) {
		Point2f b = new Point2f(v1.x - v0.x, v1.y - v0.y);
		Point2f c = new Point2f(point.x - v0.x, point.y - v0.y);
		
		float denom = b.x * b.x + b.y * b.y;
		
		return new Point2f(
			(((c.y * b.y * b.x) + (c.x * b.x * b.x)) / denom) + v0.x,
			(((c.y * b.y * b.y) + (c.x * b.x * b.y)) / denom) + v0.y
		);
	}
	
	public static float dot(Point2f a, Point2f b, Point2f c) {
		return (b.x - a.x) * (b.y - a.y) + (c.x - b.x) * (c.y - b.y) + (a.x - c.x) * (a.x - c.y);
	}
	
	public static Point2f rotate(Point2f p, float angle, float ox, float oy) {
		float C = (float)Math.cos(Math.toRadians(angle));
		float S = (float)Math.sin(Math.toRadians(angle));
		
		float x = p.x - ox;
		float y = p.y - oy;
		
		float nx = x * C - y * S;
		float ny = x * S + y * C;
		return new Point2f(nx + ox, ny + oy);
	}
	
	public static Point2f rotate(float angle, float x, float y, float ox, float oy) {
		float C = (float)Math.cos(Math.toRadians(angle));
		float S = (float)Math.sin(Math.toRadians(angle));
		
		float px = x - ox;
		float py = y - oy;
		
		float nx = px * C - py * S;
		float ny = px * S + py * C;
		return new Point2f(nx + ox, ny + oy);
	}
}

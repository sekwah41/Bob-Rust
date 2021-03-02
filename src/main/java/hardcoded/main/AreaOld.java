package hardcoded.main;

import java.awt.Point;

public class AreaOld {
	public static final int TOP_LEFT = 0;
	public static final int TOP_RIGHT = 1;
	public static final int BOTTOM_RIGHT = 2;
	public static final int BOTTOM_LEFT = 3;
	
	public Point top_left = new Point();
	public Point top_right = new Point();
	public Point bottom_right = new Point();
	public Point bottom_left = new Point();
	
	public void setCorner(int index, Point point) {
		switch(index) {
			case TOP_LEFT: top_left.setLocation(point); break;
			case TOP_RIGHT: top_right.setLocation(point); break;
			case BOTTOM_RIGHT: bottom_right.setLocation(point); break;
			case BOTTOM_LEFT: bottom_left.setLocation(point); break;
		}
	}
}

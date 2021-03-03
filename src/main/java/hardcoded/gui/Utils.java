package hardcoded.gui;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public final class Utils {
	public static final Rectangle getScreenSizeForPosition(Point point) {
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
	
	public static java.awt.geom.Area createInverse(Dimension size, Shape shape) {
		java.awt.geom.Area inverse = new java.awt.geom.Area(new Rectangle2D.Float(0, 0, size.width, size.height));
		inverse.subtract(new java.awt.geom.Area(shape));
		return inverse;
	}
}

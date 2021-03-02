package hardcoded.gui;

import java.awt.*;

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
}

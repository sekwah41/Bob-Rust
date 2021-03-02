package hardcoded.main;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

// We just have to assume that the colors are placed correctly
public class DrawImage {
	private static final BufferedImage NOISE;
	
	static {
		BufferedImage image;
		try {
			image = ImageIO.read(DrawImage.class.getResourceAsStream("/noise.png"));
		} catch(Exception e) {
			image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		}
		
		NOISE = image;
	}
	private Robot robot;
	
	public DrawImage() {
		try {
			robot = new Robot();
		} catch(Exception e) {
			
		}
	}
	
	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(Exception e) {
			
		}
	}
	public void test(BufferedImage image) {
		if(image == null) return;
		
		selectGame();
		
		int inbetween = 100;
		int wait = 100;
		
		for(int i = 0; i < 6; i++) {
			sleep(wait);
			setBrushSize(i);
		}
		sleep(inbetween);
		
		for(int i = 0; i < 4; i++) {
			sleep(wait);
			setBrushShape(i);
		}
		sleep(inbetween);
		
		for(int i = 0; i < 6; i++) {
			sleep(wait);
			setBrushOpacity(i);
		}
		sleep(inbetween);
		
		for(int i = 0; i < 4 * 8; i++) {
			sleep(wait);
			setBrushColor(i);
		}
		sleep(inbetween);
		
//		while(true) {
//			try {
//				Thread.sleep(5);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			
//			Point p = MouseInfo.getPointerInfo().getLocation();
//			System.out.println(p);
//		}
	}
	
	private static final Point[] BRUSH_SIZES = {
		new Point(1795, 340),
		new Point(1814, 340),
		new Point(1837, 340),
		new Point(1856, 340),
		new Point(1874, 340),
		new Point(1895, 340),
	};
	public void setBrushSize(int level) {
		if(level < 0) level = 0;
		if(level >= BRUSH_SIZES.length) level = BRUSH_SIZES.length - 1;
		
		Point p = BRUSH_SIZES[level];
		robot.mouseMove(p.x, p.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	private static final Point[] BRUSH_SHAPE = {
		new Point(1798, 377), // Weak halo
		new Point(1827, 377), // Circle
		new Point(1861, 377), // Strong halo
		new Point(1893, 377), // Square
	};
	public void setBrushShape(int index) {
		if(index < 0) index = 0;
		if(index >= BRUSH_SHAPE.length) index = BRUSH_SHAPE.length - 1;
		
		Point p = BRUSH_SHAPE[index];
		robot.mouseMove(p.x, p.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	private static final Point[] BRUSH_OPACITY = {
		new Point(1795, 414),
		new Point(1814, 414),
		new Point(1837, 414),
		new Point(1856, 414),
		new Point(1874, 414),
		new Point(1895, 414),
	};
	public void setBrushOpacity(int level) {
		if(level < 0) level = 0;
		if(level >= BRUSH_OPACITY.length) level = BRUSH_OPACITY.length - 1;
		
		Point p = BRUSH_OPACITY[level];
		robot.mouseMove(p.x, p.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	private static final Point COLOR_START = new Point(1797, 454);
	public void setBrushColor(int index) {
		if(index < 0) index = 0;
		if(index >= 4 * 8) index = 4 * 8 - 1;
		
		int x = ((index & 3) * 31) + COLOR_START.x;
		int y = ((index / 4) * 31) + COLOR_START.y;
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	private static final Point FOCUS_POINT = new Point(1838, 218);
	public void selectGame() {
		robot.mouseMove(FOCUS_POINT.x, FOCUS_POINT.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	private BufferedImage getBrush() {
		// 1654
		// 964
		
		Rectangle rect = new Rectangle(1654, 964, 100, 100);
		BufferedImage bi = robot.createScreenCapture(rect);
		// Subtract the noise from the image ?
		
		return bi;
	}
}

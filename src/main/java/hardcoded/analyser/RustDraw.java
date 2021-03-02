package hardcoded.analyser;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import hardcoded.analyser.RustGUIAnalyser.RColor;

public class RustDraw {
	private RustGUIAnalyser anr;
	private Robot robot;
	
	public RustDraw() throws AWTException {
		robot = new Robot();
		anr = new RustGUIAnalyser(robot);
	}
	
	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(Exception e) {
			
		}
	}
	
	public void test(Rectangle screen, BufferedImage raster) {
		if(raster == null) return;
		
		try {
			test0(screen, raster);
		} catch(Exception e) {
			// Panic break out
			e.printStackTrace();
		}
	}
	
	private void test0(Rectangle screen, BufferedImage raster) {
		anr.analyse(screen);
		
		robot.setAutoDelay(10);
		
		System.out.println("RustDraw: start");
		
		giveFocus();
		giveFocus();
		giveFocus();
		giveFocus();
		
		sleep(50);
		
		setBrushShape(Shape.STRONG_HALO);
		setBrushSize(0);
		setBrushOpacity(6);
		
		sleep(200);
		
		java.util.List<RColor> bag = new ArrayList<>();
		for(RColor r : anr.getColorButtons()) bag.add(r);
		
		
		{
			int[] points = new int[bag.size()];
			for(int i = 0; i < points.length; i++) {
				RColor r = bag.get(i);
				for(int y = 0; y < raster.getHeight(); y++) {
					for(int x = 0; x < raster.getWidth(); x++) {
						int rgb = raster.getRGB(x, y);
						if((rgb & 0xff000000) == 0) continue;
						
						RColor color = getClosest(new Color(rgb));
						if(color.equals(r)) {
							points[i]++;
						}
					}
				}
			}
			
			java.util.List<RColor> test = new ArrayList<>();
			
			for(int i = 0; i < points.length; i++) {
				int max = 0;
				int mid = -1;
				for(int j = 0; j < points.length; j++) {
					if(points[j] >= max) {
						mid = j;
						max = points[j];
					}
				}
				
				if(mid != -1 && max > 0) {
					points[mid] = -1;
					test.add(bag.get(mid));
				}
			}
			
			bag.clear();
			bag.addAll(test);
		}
		
		int skip = 2;
		// RColor last_rc = null;
		for(RColor r : bag) {
			setBrushColor(r);
			for(int y = 0; y < raster.getHeight(); y += skip) {
				for(int x = 0; x < raster.getWidth(); x += skip) {
					int rgb = raster.getRGB(x, y);
					if((rgb & 0xff000000) == 0) continue;
					
					RColor color = getClosest(new Color(rgb));
					if(!color.equals(r)) {
						continue;
					}
					
					// TODO: Correct monitor!
					doClick(new Point(x, y));
				}
			}
		}
	}
	
	private void giveFocus() {
		doClick(anr.getFocusPoint());
		sleep(50);
	}
	
	public void setBrushOpacity(int level) {
		doClick(anr.getOpacityButtons(), level);
	}
	
	public void setBrushSize(int level) {
		doClick(anr.getSizeButtons(), level);
	}
	
	public void setBrushShape(Shape shape) {
		for(int i = 0; i < 3; i++) {
			doClick(anr.getShapeButtons(), shape.ordinal());
		}
	}
	
	public void setBrushColor(Color color) {
		setBrushColor(getClosest(color));
	}
	
	public void setBrushColor(RColor color) {
		if(color == null) return;
		doClick(color.getPoint());
	}
	
	private void doClick(Point[] array, int index) {
		if(index < 0) index = 0;
		if(index >= array.length) index = array.length - 1;
		
		doClick(array[index]);
	}
	
	private void doClick(Point p) {
		robot.mouseMove(p.x, p.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		sleep(10);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		Point next = MouseInfo.getPointerInfo().getLocation();
		if(next.x != p.x || next.y != p.y) {
			throw new IllegalStateException("Mouse was moved during the execution of the program.. Pause?");
		}
	}
	
	public RColor getClosest(Color color) {
		RColor[] array = anr.getColorButtons();
		
		RColor best = null;
		float score = Float.MAX_VALUE;
		for(RColor rc : array) {
			Color cc = rc.getColor();
			float rd = (color.getRed() - cc.getRed()) / 255.0f;
			float gd = (color.getGreen() - cc.getGreen()) / 255.0f;
			float bd = (color.getBlue() - cc.getBlue()) / 255.0f;
			float cs = (rd * rd + gd * gd + bd * bd);
			
			if(cs < score) {
				score = cs;
				best = rc;
			}
		}
		
		return best;
	}
	
	public enum Shape {
		SOFT_HALO,
		CIRCLE,
		STRONG_HALO,
		SQUARE
	}
}

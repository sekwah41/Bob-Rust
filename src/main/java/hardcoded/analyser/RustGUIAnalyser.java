package hardcoded.analyser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to analyse the gui of rust to get information about the game
 * @author HardCoded
 */
public class RustGUIAnalyser {
	private final Robot robot;
	
	private List<RColor> colors = new ArrayList<>();
	
	public RustGUIAnalyser(Robot robot) {
		this.robot = robot;
	}
	
	private Point panel_offset;
	// Start on HD screens (1770, 277)
	// The size is 150 x 525
	public void analyse(Rectangle screen) {
		// How do we know where stuff is?
		// TODO: What if the screen is not (1920x1080) ?
		panel_offset = new Point(1770, 277);
		panel_offset.x += screen.x;
		panel_offset.y += screen.y;
		
		Rectangle rect = new Rectangle(1770, 277, 150, 525);
		BufferedImage image = robot.createScreenCapture(rect);
		
		// Colors start
		
		// ( 12, 166)
		// ( 44, 166)
		// ( 76, 166)
		// (108, 166)
		//
		// ( 12, 196) // 30
		
		// ( 76, 226) // 30
		
		for(int x = 0; x < 4; x++) {
			for(int y = 0; y < 8; y++) {
				int x_pos = 27 + x * 32;
				int y_pos = 180 + y * 30;
				
				int rgb = image.getRGB(x_pos, y_pos);
				RColor color = new RColor(
					new Color(rgb),
					point(x_pos, y_pos)
				);
				
				if(!colors.contains(color)) {
					colors.add(color);
				}
			}
		}
		
		System.out.println(colors.size() + " unique colors");
		
		// JOptionPane.showConfirmDialog(null, new JLabel(new ImageIcon(image)), "Is this the color pallete?", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Returns a spot were the bot can press without changing any state of the game.
	 */
	public Point getFocusPoint() {
		return point(12, 24);
	}
	
	public Point getClearButton() {
		return point(55, 24);
	}
	
	public Point getSaveButton() {
		return point(95, 24);
	}
	
	public Point getUpdateButton() {
		return point(75, 469);
	}
	
	public Point getCancelButton() {
		return point(75, 505);
	}
	
	public Point[] getOpacityButtons() {
		return new Point[] {
			point( 22, 138),
			point( 43, 138),
			point( 64, 138),
			point( 85, 138),
			point(106, 138),
			point(127, 138),
		};
	}
	
	public Point[] getSizeButtons() {
		return new Point[] {
			point( 25, 62),
			point( 45, 62),
			point( 65, 62),
			point( 85, 62),
			point(105, 62),
			point(125, 62),
		};
	}
	
	public Point[] getShapeButtons() {
		return new Point[] {
			point( 27, 100), // Soft halo
			point( 59, 100), // Circle
			point( 91, 100), // Strong halo
			point(123, 100), // Square
		};
	}
	
	public RColor[] getColorButtons() {
		return colors.toArray(new RColor[0]);
	}
	
	private Point point(int x, int y) {
		return new Point(panel_offset.x + x, panel_offset.y + y);
	}
	
	public static class RColor {
		private final Color color;
		private final Point point;
		
		public RColor(Color color, Point point) {
			this.color = color;
			this.point = point;
		}
		
		public int hashCode() {
			return color.getRGB();
		}
		
		public boolean equals(Object obj) {
			if(!(obj instanceof RColor)) return false;
			return hashCode() == obj.hashCode();
		}
		
		public Color getColor() {
			return color;
		}
		
		public Point getPoint() {
			return point;
		}
		
		@Override
		public String toString() {
			return String.format("RColor(%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue());
		}
	}
}

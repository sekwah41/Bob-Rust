package hardcoded.widget;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public interface ImageWidget {
	/**
	 * Used for drawing this widget on screen.
	 * 
	 * @param g the graphics context
	 * @param image the image
	 */
	void draw(Graphics2D g);
	
	/**
	 * Used for drawing the widget overlay.
	 * 
	 * @param g the graphics context
	 * @param image the image
	 */
	void drawOverlay(Graphics2D g);
	
	/**
	 * Change the image of this widget.
	 */
	void setImage(BufferedImage image);
	
	/**
	 * Returns the image of this widget.
	 * @return the image of this widget
	 */
	BufferedImage getImage();
	
	/**
	 * Returns if this widget is visible.
	 * @return if this widget is visible
	 */
	boolean isVisible();
	
	/**
	 * Change if this vidget is visible.
	 * 
	 * @param enable the state
	 */
	void setVisible(boolean enable);
	
	
	void onMousePressed(MouseEvent e);
	void onMouseRelease(MouseEvent e);
	void onMouseMove(MouseEvent e);
	void onMouseDrag(MouseEvent e);
}

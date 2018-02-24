/*
 * Sprite.java
 * 
 * This is the exact same class as used in part 1.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {

	private GraphicsConfiguration gc;
	private BufferedImage img;
	private Point position, focus;
	private int orient;
	private double scale;
	
	// constructor loads the passed file for its image
	public Sprite(String filename) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		
		position = new Point();
		focus = new Point();
		orient = 0;
		scale = 1.0;
		
		if (filename != null) {
			img = loadImage(filename);
		}
	} // end of constructor
	
	// constructor loads no image
	public Sprite() {
		this(null);
	} // end of constructor
	
	// loads the image file associated with the passed string
	private BufferedImage loadImage(String filename) {
		try {
			BufferedImage bi = ImageIO.read(new File(filename));
			int transparency = bi.getColorModel().getTransparency();
			BufferedImage copy = gc.createCompatibleImage(bi.getWidth(), bi.getHeight(), transparency);
			Graphics2D g2d = copy.createGraphics();
			g2d.drawImage(bi, 0, 0, null);
			g2d.dispose();
			return copy;
		} catch(IOException e) {
			System.out.println("Failed to load Sprite image. " + e);
			return null;
		}
	} // end of loadImage
	
	// draw the image to screen based on the associated values
	public void drawImage(Graphics2D g2d) {
		g2d.rotate(Math.toRadians(orient), position.x + img.getWidth() / 2, position.y + img.getHeight() / 2);
		g2d.scale(scale, scale);
		g2d.drawImage(img, null, (int)((position.x - focus.x) * 1 / scale), (int)((position.y - focus.y) * 1 / scale));
		g2d.scale(img.getWidth() / (img.getWidth() * scale), img.getHeight() / (img.getHeight() * scale));
		g2d.rotate(Math.toRadians(-orient), position.x + img.getWidth() / 2, position.y + img.getHeight() / 2);
	} // end of drawImage
	
	// setters and getters...
	public Point getFocus() { return focus;	}
	
	public BufferedImage getImage() { return img; }
	
	public Point getPosition() { return position; }
	
	public int getOrient() { return orient; }
	
	public double getScale() { return scale; }
	
	public void setFocus(Point pt) { focus = new Point(pt); }
	
	public void setFocus(int x, int y) { focus = new Point(x, y); }
	
	// sets the sprite focus point to the middle of the image
	public void setFocusMid() { focus = new Point(img.getWidth() / 2, img.getHeight() / 2); }
	
	// sets the sprite focus point to the upper left corner
	public void setFocusTopLeft() { focus = new Point(); }
	
	// loads and sets the image of the sprite to the associated file
	public void setImage(String filename) { img = loadImage(filename); }
	
	// check and set the position to the new value
	public boolean setPosition(Point newPosition) {
		if (newPosition.x < 0 || newPosition.y < 0) {
			return false;
		}
		position = new Point(newPosition);
		return true;
	} // end of setPosition
	
	// check and set the orientation to the new value
	public boolean setOrientDegrees(int newOrient) {
		if (newOrient < 0 || newOrient > 360) {
			return false;
		}
		orient = newOrient;
		return true;
	} // end of setOrientDegrees
	
	// check and set the scaling scale the the new value
	public boolean setScale(double newScale) {
		if (newScale < 0.0 || newScale > 1.0) {
			return false;
		}
		scale = newScale;
		return true;
	} // end of setSize(double)
	
	public boolean setScaleByPx(int pixels) {
		if (pixels <= 0) {
			return false;
		}
		scale = (double)pixels / img.getWidth(); 
		return true;
	} // end of setSize(double)
	
} // end of class Sprite

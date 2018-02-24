/*
 * Sprite.java
 * 
 * This class is used to represent the game entities.  It is used to store and draw the image file associated with a game
 * object.  It also tracks physical properties with a Kinematic class, and uses a Rectangle as a hit box for collision
 * detection with the object.  Additionally, a focus point is used to allow the position of the entity to be tracked from
 * any point, not just the top corner of the image.  Several of these variables are protected so that the class can be
 * sub-classed if needed.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package part2.spacepizza;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import part2.spacepizza.steering.Kinematic;

public class Sprite {

	private GraphicsConfiguration gc;
	protected BufferedImage img;
	protected Kinematic kin;
	private Rectangle hitBox;
	private Point2d boxSize;
	protected Point2d focus;
	
	// constructor loads the passed file for its image
	public Sprite(String filename) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		
		kin = new Kinematic();
		
		if (filename != null) {
			img = loadImage(filename);
			hitBox = new Rectangle((int)kin.position.x, (int)kin.position.y, img.getWidth(), img.getHeight());
		}
		
		boxSize = new Point2d(1.0, 1.0);
		focus = new Point2d();
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
	
	// draw the image to screen based on the associated kinematic values
	public void drawImage(Graphics2D g2d) {
		g2d.rotate(kin.orientation, kin.position.x + img.getWidth() / 2, kin.position.y + img.getHeight() / 2);
		g2d.drawImage(img, null, (int)(kin.position.x - focus.x), (int)(kin.position.y - focus.y));
		g2d.rotate(-kin.orientation, kin.position.x + img.getWidth() / 2, kin.position.y + img.getHeight() / 2);
		hitBox = new Rectangle((int)(kin.position.x - focus.x), (int)(kin.position.y - focus.y), img.getWidth(), img.getHeight());
	} // end of drawImage
	
	// creates a square hit box based on the passed ratio value
	public void setHitBoxSize(double ratio) {
		if (ratio < 0.0 || ratio > 1.0) {
			return;
		}
		
		boxSize.x = ratio;
		boxSize.y = ratio;
		adjustHitBox();
	} // end of setHitBoxSize(double)
	
	// sets the size of the hit box associated with the sprite based on the passed ratio values
	public void setHitBoxSize(double wRatio, double hRatio) {
		if (wRatio >= 0.0 || wRatio <= 1.0) {
			boxSize.x = wRatio;
		}
		
		if (hRatio >= 0.0 || hRatio <= 1.0) {
			boxSize.y = hRatio;
		}
		
		adjustHitBox();
	} // end of setHitBox(double, double)
	
	// sets the specific size of the hit box associated with the sprite based on the passed values
	public void setHitBoxSize(int width, int height) {
		if (width <= 0 || width > img.getWidth() ||
			height <= 0 || height > img.getHeight()) {
			
			return;
		}
		
		boxSize.x = img.getWidth() / width;
		boxSize.y = img.getHeight() / height;
		adjustHitBox();
	} // end of setHitBoxSize(int, int)
	
	// adjusts the rectangle hit box according to the image size and position
	private void adjustHitBox() {
		int width = (int)(img.getWidth() * boxSize.x);
		int height = (int)(img.getHeight() * boxSize.y);
		
		hitBox.x = (int)(kin.position.x + (img.getWidth() - width) / 2);
		hitBox.width = width;
		hitBox.y = (int)(kin.position.y + (img.getHeight() - height) / 2);
		hitBox.height = height;
	} // end of adjustHitBox
	
	// checks if a passed point lies within the hit box
	public Boolean hitRegister(int x, int y) {
		return hitBox.contains(x, y);
	} // end of hitRegister(int, int)
	
	// checks if a passed point lies within the hit box
	public Boolean hitRegister(double x, double y) {
		return hitBox.contains(x, y);
	}  // end of hitRegister(double, double)
	
	// checks if the passed rectangle overlaps with the hit box
	public Boolean hitRegister(Rectangle r) {
		return hitBox.intersects(r);
	}  // end of hitRegister(int, int)
	
	// returns the rectangle used to represent the hit box
	public Rectangle getHitBox() {
		return hitBox;
	} // end of getHitBox
	
	// returns the image associated with this sprite
	public BufferedImage getImage() {
		return img;
	} // end of getImage
	
	// returns the kinematic associated with this sprite
	public Kinematic getKinematic() {
		return kin;
	}  // end of getKinematic
	
	// loads and sets the image of the sprite to the associated file
	public void setImage(String filename) {
		img = loadImage(filename);
		hitBox = new Rectangle((int)kin.position.x, (int)kin.position.y, img.getWidth(), img.getHeight());
	} // end of setImage
	
	// replaces the current kinematic with the passed kinematic 
	public void setKinematic(Kinematic k) {
		kin = k;
	} // end of setKinematic
} // end of class Sprite

/*
 * Player.java
 * 
 * This class is used to represent the player in the game.  The player is represented by a pizza image.  The player begins
 * with a full pizza. Each time it is hit by an enemy weapon, it reduces in size, and shows a new corresponding sprite
 * image.  Once the pizza slice is 1/8 of the full pizza, the next hit will cause the game to end.  This is subclass of the
 * Sprite class, used to store the image/position/etc. of the player entity.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza;

import java.awt.*;
import javax.vecmath.*;

import spacepizza.steering.Arrive;
import spacepizza.steering.Kinematic;
import spacepizza.steering.Seek;

public class Player extends Sprite {
	
	private static final double MAX_ACCL = 0.1;
	private static final double MAX_SPD = 1.5;
	
	private static final String FULL = "images/pizza1.png";
	private static final String HALF = "images/pizza2.png";
	private static final String QUARTER = "images/pizza3.png";
	private static final String EIGHTH = "images/pizza4.png";
	
	private Seek seek;
	private Arrive arrive;
	private Rectangle boundary;
	private int hitPoints;
	
	public Player(int bWidth, int bHeight) {
		super(FULL);
		seek = new Seek(kin, MAX_ACCL);
		arrive = new Arrive(kin, MAX_ACCL, MAX_SPD, 5.0, 5.0, 0.1);
		boundary = new Rectangle(0, 0, bWidth, bHeight);
		hitPoints = 3;
		setHitBox();
		focus = new Point2d(img.getWidth() / 2, img.getHeight() / 2);
		kin.position = new Vector2d(focus);
		kin.target = new Vector2d(focus);
	} // end of constructor
	
	// update steering and kinematic for pizza sprite
	public void update(long time) {
		seek.setObject(kin);
		arrive.setObject(kin);
		kin.update(arrive.getSteering(), MAX_SPD, time / 1000000);
	} // end of update
	
	// set the target for the pizza to the passed integer coordinates
	public void setTarget(int x, int y) {
		// check if target in player boundary
		if (boundary.contains(x, y)) {
			
			// adjust x value image width
			if (x < img.getWidth() / 2) {
				kin.target.x = img.getWidth() / 2;
			} else if (x > boundary.getMaxX() - img.getWidth() / 2) {
				kin.target.x = boundary.getMaxX() - img.getWidth() / 2;
			} else {
				kin.target.x = x;
			}
			
			// adjust y value for image height
			if (y < img.getHeight() / 2) {
				kin.target.y = img.getHeight() / 2;
			} else if (y > boundary.getMaxY() - img.getHeight() / 2) {
				kin.target.y = boundary.getMaxY() - img.getHeight() / 2;
			} else {			
				kin.target.y = y;
			}
		} // end if
	} // end of setTarget
	
	// draw the pizza image
	public void draw(Graphics2D g2d) {
		if (kin.inTargetRange()) {
			kin.position = new Vector2d(kin.target);
			kin.velocity = new Vector2d();
		}
		this.drawImage(g2d);
	} // end of draw
	
	// adjust the hit points for the pizza character and change to the appropriate image
	public Boolean hit() {
		switch(--hitPoints) {
		case 0:
			setImage(EIGHTH);
			break;
		case 1:
			setImage(QUARTER);
			break;
		case 2:
			setImage(HALF);
			break;
		default:
			return false;	
		}
		
		focus = new Point2d(img.getWidth() / 2, img.getHeight() / 2);
		setHitBox();
		return true;
	} // end of hit()
	
	// sets the hit box for the currently visible player sprite
	private void setHitBox() {
		switch (hitPoints) {
		case 0:
			setHitBoxSize(img.getWidth() - 4, img.getHeight() - 4);
			return;
		case 1:
			setHitBoxSize(img.getWidth() - 2, img.getHeight() - 2);
			return;
		case 2:
			setHitBoxSize(img.getWidth() - 2, img.getHeight() - 2);
			return;
		case 3:
			setHitBoxSize(0);
			return;
		}
	} // end of setHitBox
	
	// returns a kinematic with the current position just behind the player sprite to act as centre of topping flock
	public Kinematic getFlockTo() {
		Kinematic flockTo = new Kinematic(kin);
		flockTo.position.sub(new Vector2d(img.getWidth(),0));
		return flockTo;
	} // end of getFlockTo
} // end of class Player

/*
 * Weapon.java
 * 
 * This class is used to represent the enemy weapon entities in the game.  There are 2 weapon types in the game - a knife
 * and a pizza cutter.  Additionally, each weapon type has a state representing if the enemy character is holding it or if
 * it has been thrown at the player.  Another state represents if the weapon is currently following the player.  A thrown 
 * knife will travel in a straight line from the enemy to the closest point off screen through the current player position.
 * A thrown pizza cutter will track the player position for a few seconds before heading on its current trajectory to a
 * point off screen.  Distances and targets for these states are all calculated in this class.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package part2.spacepizza;

import javax.vecmath.Vector2d;

import part2.spacepizza.steering.Kinematic;

public class Weapon extends Sprite {

	public static final int KNIFE = 0;
	public static final int CUTTER = 1;

	private int type;
	private Boolean held;
	private Boolean following;
	private long beginTime;
	private HillClimber hc;
	
	public Weapon(int type, Boolean held) {
		this.type = type;
		this.held = held;
		beginTime = System.nanoTime();
		
		this.setImage(getFilename(type, held));
		
		setFocus();
		
		if (type == CUTTER) {
			following = true;
		} else {
			following = false;
		}
		
		hc = new HillClimber();
	} // end of constructor
	
	// sets the target for the weapon to the passed coordinates
	public void setTarget(double x, double y, Kinematic enemyKin) {
		kin.target = new Vector2d(x, y);

		// adjust target with hill climbing algorithm
		if (kin != null) {
			hc.setKinematic(enemyKin);
			kin.target = hc.hClimb(new Vector2d(kin.target));
		}
	} // end of setTarget(double, double)
	
	// calculates the target for the weapon to a point off screen and through the pizza position
	public void setTarget(double initX, double initY, double pizzaX, double pizzaY) {
		kin.position.x = initX;
		kin.position.y = initY;		
		double slope = (pizzaY - kin.position.y) / (pizzaX - kin.position.x);
		// what's less to x0, y0 or yMAX?
		
		// x = 0
		double targetMinX = (-img.getWidth() - pizzaX) * slope + pizzaY;
		// y = 0
		double targetMinY = (-pizzaY - img.getHeight()) / slope + pizzaX;
		// y = MAX
		double targetMaxY = (MainPanel.PHEIGHT + img.getHeight() - pizzaY) / slope + pizzaX;
		
		// compare calculated values and use the minimum distance as target
		if (getDistance(initX, initY, 0, targetMinX) < getDistance(initX, initY, targetMinY, 0) &&
				getDistance(initX, initY, 0, targetMinX) < getDistance(initX, initY, targetMinY, 1280)) {
			kin.target = new Vector2d(-img.getWidth() , targetMinX);
		} else if (initY > pizzaY) {
			kin.target = new Vector2d(targetMinY, -img.getHeight());
		} else {
			kin.target = new Vector2d(targetMaxY, MainPanel.PHEIGHT + img.getHeight());
		} // end else
		
	} // end of setTarget(double, double, double, double)
	
	// calculates the off screen target for the current trajectory
	public void unFollowing() {
		double newTargetX, newTargetY, xValue, yValue;
		double slope = (kin.target.y - kin.position.y) / (kin.target.x - kin.position.x);
		
		// off to the left of the screen
		if (kin.position.x < kin.target.x) {
			newTargetY = (MainPanel.PWIDTH + img.getWidth() - kin.target.x) * slope + kin.target.y;
			xValue = MainPanel.PWIDTH + img.getWidth();
		} else {  // off the right of the screen
			newTargetY = (-kin.target.x - img.getWidth()) * slope + kin.target.y;;
			xValue = -img.getWidth();
		}
		
		// off to the top of the screen
		if (kin.position.y < kin.target.y) {
			newTargetX = (MainPanel.PHEIGHT + img.getHeight() - kin.target.y) / slope + kin.target.x;
			yValue = MainPanel.PHEIGHT + img.getHeight();
		} else { // off the bottom of the screen
			newTargetX = (-kin.target.y - img.getHeight()) / slope + kin.target.x;
			yValue = -img.getHeight();
		}
		
		// compare distances and use the minimum as target
		if (getDistance(kin.position.x, kin.position.y, xValue, newTargetY) < getDistance(kin.position.x, kin.position.y, newTargetX, yValue)) {
			kin.target = new Vector2d(xValue, newTargetY);
		} else {
			kin.target = new Vector2d(newTargetX, yValue);
		} // end else
	} // end unFollowing
	
	// returns the distance between points 
	private double getDistance(double initX, double initY, double targetX, double targetY) {
		return Math.sqrt(Math.pow((initX - targetX), 2) + Math.pow((initY - targetY), 2));
	} // end of getDistance
	
	// switches the weapon type and changes the associated image
	public void switchWeapon() {
		if (type == 0) {
			type = 1;
			this.setImage(getFilename(type, held));
			setFocus();
		} else {
			type = 0;
			this.setImage(getFilename(type, held));
			setFocus();
		} // end else
	} // end of switchWeapon
	
	// returns the string for the file name associated with the weapon type and if it's held/thrown
	private String getFilename(int type, Boolean held) {
		String filename = "images/";
		switch(type) {
		case 0:
			filename += "knife";
			break;
		case 1:
			filename += "cut";
			break;
		}
		
		if (held) {
			filename += "2";
		} else {
			filename += "1";
		}
		return filename + ".png";
	} // end of getFilename
	
	// sets the focus point associated with the kinematic based on if the weapon is held/thrown
	private void setFocus() {
		if (held) {
			focus.x = img.getWidth() / 2;
			focus.y = img.getHeight();
		} else {
			focus.x = img.getWidth();
			focus.y = img.getHeight() /2;
		} // end else
	} // end of setFocus
	
	// returns the type of weapon
	public int getType() { return type; }
	
	// returns if weapon held/thrown
	public Boolean isHeld() { return held; }
	
	// returns the time when object created
	public long getBeginTime() { return beginTime; }
	
	// returns if the weapon is following the player or not
	public Boolean getFollowing() { return following; }
	
	// changes the following state to the passed value
	public void setFollowing(Boolean newValue) { following = newValue; }
} // end of class Weapon

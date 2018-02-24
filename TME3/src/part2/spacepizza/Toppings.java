/*
 * Toppings.java
 * 
 * This class is used to handle the topping entities that appear in game as collectibles, and as a flock following the
 * the player entity.  Each individual topping is represented using a Sprite, or a subclass.  This subclass is the inner
 * class Topping seen below.  The flock of toppings is meant to follow the player around the screen and show how many of
 * each topping type have been caught by the player.  To catch a topping, it will randomly appear on the screen and the
 * player must move within range.  This causes the count (and thus the player score) to increase.  Collecting 10 or more
 * of each type will end the game.  There are a total of 6 topping types.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package part2.spacepizza;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import javax.vecmath.Vector2d;

import part2.spacepizza.steering.Flock;
import part2.spacepizza.steering.Kinematic;
import part2.spacepizza.steering.Seek;

public class Toppings {
	// an inner class that extends the Sprite class to represent each individual topping as a unique entity
	public class Topping extends Sprite {
		public static final int TIME_TO_CATCH = 4000;
		public int type;
		public boolean caught;
		public long catchCount;
		
		public Topping(int type) {
			super(getToppingFilename(type));
			this.type = type;
			caught = false;
			catchCount = System.currentTimeMillis();
		} // end of constructor
	} // end of inner class Topping
		
	public static final int NUM_TOPPINGS = 6;
	public static final int PEPPERONI = 0;
	public static final int SAUSAGE = 1;
	public static final int BACON = 2;
	public static final int G_PEPPER = 3;
	public static final int MUSHROOM = 4;
	public static final int ONION = 5;
	
	public static final double MAX_ACCL = 0.5;
	public static final double MAX_SPD = 1.8;
	
	private Sprite[] flockToppings;
	private int[] numToppings;
	private Vector<Topping> catchToppings;
	private Random rand;
	private Seek seek;
	private Flock flock;
	
	public Toppings() {
		// add sprites to topping flock
		flockToppings = new Sprite[NUM_TOPPINGS];
		numToppings = new int[NUM_TOPPINGS];
		for (int i = 0; i < NUM_TOPPINGS; i++) {
			flockToppings[i] = new Sprite(getToppingFilename(i));
			flockToppings[i].kin.position = new Vector2d(i*20,i*1);
			numToppings[i] = 0;
		}
		
		catchToppings = new Vector<Topping>();
		rand = new Random();
		seek = new Seek(new Kinematic(), MAX_ACCL);
		flock = new Flock();
	} // end of constructor
	
	// returns the image file string associated with a given topping value
	private String getToppingFilename(int top) {
		switch (top) {
			case 0:
				return "images/pepperoni.png";
			case 1:
				return "images/sausage.png";
			case 2:
				return "images/bacon.png";
			case 3:
				return "images/gpepper.png";
			case 4:
				return "images/mushroom.png";
			case 5:
				return "images/onion.png";
			default:
				return null;
		}
	} // end of getToppingFilename
	
	// adds a new topping to the game field at a random position as a collectible for the player
	public void addNew(int maxWidth, int maxHeight) {
		int randX, randY, randTop;
		// choose random values
		randTop = rand.nextInt(NUM_TOPPINGS);
		randX = rand.nextInt(maxWidth - flockToppings[randTop].img.getWidth() + 1) + flockToppings[randTop].img.getWidth();
		randY = rand.nextInt(maxHeight - flockToppings[randTop].img.getHeight() + 1) + flockToppings[randTop].img.getHeight();
		
		// create new topping at rand position
		Topping newTop = new Topping(randTop);
		newTop.kin.position = new Vector2d(randX, randY);
		
		// add to list
		catchToppings.addElement(newTop);
	} // end of addNew
	
	// draws the flock of toppings behind the player with current count values, as well as all collectible toppings
	public void draw(Graphics2D g2d) {
		for (int i = 0; i < flockToppings.length; i++) {
			//if (numToppings[i] > 0) {
				flockToppings[i].drawImage(g2d);
				drawToppingNum(g2d, i);
			//}
		}
		// draw toppings to catch
		for (Iterator<Topping> i = catchToppings.iterator(); i.hasNext();) {
			Topping top = i.next();
			top.drawImage(g2d);
		} // end for loop
	} // end of draw
	
	// draw the number of toppings caught by the player for a given topping at the correct position next to the sprite image
	private void drawToppingNum(Graphics2D g2d, int top) {
		// calculate draw position
		Vector2d topPosition = new Vector2d(flockToppings[top].kin.position);
		topPosition.x += flockToppings[top].img.getWidth() + 1;
		
		// draw number next to image
		String topNum = "x" + numToppings[top];
		g2d.setColor(Color.WHITE);
		g2d.drawString(topNum, (int)topPosition.x, (int)topPosition.y);
	} // end of drawToppingNum
	
	// update steering and kinematic values for all toppings currently visible on the game field
	public void update(long time, Kinematic flockTo) {
		// calculate flock topping values
		flock.adjustFlock(flockToppings, flockTo, time / 1000000);
		
		// check free toppings parameters and values
		long currTime = System.currentTimeMillis();
		for (int i = 0; i < catchToppings.size(); i++) {
			Topping top = (Topping) catchToppings.get(i);			
			// check caught topping in range and remove if needed
			if (top.caught && top.hitRegister(flockToppings[top.type].getHitBox())) {
				++numToppings[top.type];
				catchToppings.remove(i);
				--i;
				continue;
			}
			// check if topping too long and not caught
			if (!top.caught && currTime - top.catchCount > Topping.TIME_TO_CATCH) {
				catchToppings.remove(i);
				--i;
				continue;
			}
			// calculate new values for caught topping
			if (top.caught) {
				top.kin.target = flockToppings[top.type].kin.position;
				seek.setObject(top.kin);
				top.kin.update(seek.getSteering(), MAX_SPD, time / 1000000);
			} // end if
		} // end for loop
	} // end of update
	
	// checks if topping has been caught using collision detection
	public void checkForCatch(Rectangle area) {
		for (Iterator<Topping> i = catchToppings.iterator(); i.hasNext();) {
			Topping top = i.next();
			if (top.hitRegister(area)) {
				top.caught = true;
				top.kin.target = flockToppings[top.type].kin.position;
			} // end if
		} // end for loop
	} // end of checkForCatch
	
	// returns true if player has caught 10 or more of each topping type
	public boolean full() {
		for (int i = 0; i < numToppings.length; i++) {
			if (numToppings[i] < 10) {
				return false;
			}
		}
		return true;
	} // end of full
	
	// returns the total number of toppings caught by the player
	public int getScore() {
		int score = 0;
		for (int i = 0; i < numToppings.length; i++) {
			score += numToppings[i];
		}
		return score;
	} // end of getScore
} // end of class Toppings

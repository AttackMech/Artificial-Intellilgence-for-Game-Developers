/*
 * Enemy.java
 * 
 * This class is used to represent the enemy player in the game.  The player will see a pizza chef moving up and down the
 * right side of the screen, with a weapon in hand.  Every so often the enemy will launch a weapon towards the player.
 * Most often a knife is launched which travels on a line through the current player position and off screen.  However,
 * 5th weapon launched is a pizza cutter.  This weapon will follow the player position for a number of seconds.  Once this
 * time is over, the weapon will continue on its current course until it leaves the screen.  This is subclass of the
 * Sprite class, used to store the image/position/etc. of the player entity.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package part2.spacepizza;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.awt.Graphics2D;

import javax.vecmath.Vector2d;

import part2.spacepizza.steering.Kinematic;
import part2.spacepizza.steering.LookWhereGoing;
import part2.spacepizza.steering.Seek;
import part2.spacepizza.steering.SteeringOutput;

public class Enemy extends Sprite {
	
	private static final int HAND_X = 10;
	private static final int HAND_Y = 115;
	
	private static final double MAX_ACCL = 1;
	private static final double MAX_SPD = 1;
	
	private static final double KNF_MAX_ACCL = 1;
	private static final double KNF_MAX_SPD = 0.8;
	
	private static final double CUT_MAX_ACCL = 0.2;
	private static final double CUT_MAX_SPD = 0.3;
	private static final double CUT_MAX_ROT = 0.005;
	private static final double CUT_TGT_RAD = Math.toRadians(3);
	private static final double CUT_SLW_RAD = Math.toRadians(10);
	private static final double CUT_MAX_ANG_ACCL = 0.001;
	
	private Seek imgSeek, wpnSeek;
	private LookWhereGoing wpnLook;
	private Rectangle boundary;
	private Weapon handWeapon;
	private ArrayList<Weapon> thrownWeapons;
	private long weaponNum;
	
	public Enemy(int width, int height) {
		super("images/man1.png");
		
		imgSeek = new Seek(kin, MAX_ACCL);
		wpnSeek = new Seek(new Kinematic(), KNF_MAX_ACCL);
		wpnLook = new LookWhereGoing(CUT_MAX_ANG_ACCL, CUT_MAX_ROT, CUT_TGT_RAD, CUT_SLW_RAD, 0.1);
		
		boundary = new Rectangle(img.getWidth(), height - img.getHeight() - 10);
		boundary.setLocation(width - img.getWidth(), 5);
		
		kin.position = new Vector2d(width - img.getWidth(), 0);
		kin.target = new Vector2d(boundary.getCenterX(), boundary.getMaxY());
		
		handWeapon = new Weapon(Weapon.KNIFE, true);
		thrownWeapons = new ArrayList<Weapon>();
		weaponNum = 0;
	} // end of constructor
	
	// update positions, velocities, etc. of enemy associated entities
	public void update(long time, Kinematic player) {
		// update character image
		imgSeek.setObject(kin);
		kin.update(imgSeek.getSteering(), MAX_SPD, time / 1000000);
		if (kin.position.y <= boundary.getMinY()) {
			kin.target = new Vector2d(boundary.getMinX(), boundary.getMaxY());
		} else if (kin.position.y >= boundary.getMaxY()) {
			kin.target = new Vector2d(boundary.getMinX(), boundary.getMinY());
		}
		
		// update held weapon
		handWeapon.kin.position.x = this.kin.position.x + HAND_X;
		handWeapon.kin.position.y = this.kin.position.y + HAND_Y;
		
		// update thrown weapons
		for (int i = 0; i < thrownWeapons.size(); i++) {
			Weapon wpn = thrownWeapons.get(i);
			
			// set cutter targets if following player or not
			if (wpn.getType() == Weapon.CUTTER && System.nanoTime() / 1000000 - wpn.getBeginTime() / 1000000 > 5000) {
				wpn.setFollowing(false);
				wpn.unFollowing();
			} else if (wpn.getType() == Weapon.CUTTER && wpn.getFollowing()) {
				wpn.setTarget(player.position.x, player.position.y, player);
			} else {
				wpn.kin.orientation = Math.atan2(-wpn.kin.velocity.y, -wpn.kin.velocity.x);
			}
			
			// get steering values for weapon
			wpnSeek.setObject(wpn.kin);
			SteeringOutput s = wpnSeek.getSteering();
			wpnLook.setObject(wpn.kin);
			
			// update weapon kinematics
			if (wpn.getType() == Weapon.KNIFE) {
				wpn.kin.update(s, KNF_MAX_SPD, time / 1000000);
			} else {
				wpn.kin.update(wpnSeek.getSteering(CUT_MAX_ACCL), CUT_MAX_SPD, time / 1000000);
				wpn.kin.update(wpnLook.getSteering(), CUT_MAX_SPD, time / 1000000);
			}

			// remove when off screen
			if (wpn.kin.position.x < -wpn.img.getWidth() ||
				wpn.kin.position.x > MainPanel.PWIDTH ||
				wpn.kin.position.y < -wpn.img.getHeight() ||
				wpn.kin.position.y > MainPanel.PHEIGHT) {
				thrownWeapons.remove(i);
				--i;
			} // end if
		} // end for loop
	} // end of update
	
	// used to draw enemy entities
	public void draw(Graphics2D g2d) {
		// draw character and weapon
		handWeapon.drawImage(g2d);
		this.drawImage(g2d);
		
		// draw thrown weapons
		for (int i = 0; i < thrownWeapons.size(); i++) {
			Weapon wpn = thrownWeapons.get(i);
			wpn.drawImage(g2d);
		}
	} // end of draw
	
	// adds a new weapon to the game field, every 5th is a pizza cutter
	public void fireWeapon(double x, double y) {
		Weapon firing;
		if (++weaponNum % 5 != 0) {  // add a new knife
			firing = new Weapon(Weapon.KNIFE, false);
			firing.setTarget(kin.position.x + HAND_X, kin.position.y + HAND_Y, x, y);
			if (weaponNum % 5 == 4) {
				handWeapon.switchWeapon();
			}
		} else { // add a new pizza cutter
			firing = new Weapon(Weapon.CUTTER, false);
			firing.kin.position = new Vector2d(kin.position.x + HAND_X, kin.position.y + HAND_Y);
			firing.setTarget(x, y, new Kinematic());
			handWeapon.switchWeapon();
		}	
		// add to weapon list
		thrownWeapons.add(firing);
	} // end of fireWeapon
	
	// collision detection for each weapon on the game field
	public boolean checkForHits(Rectangle area) {
		boolean hitResult = false;
		for (int i = 0; i < thrownWeapons.size(); i++) {
			Weapon wpn = thrownWeapons.get(i);
			if (wpn.hitRegister(area)) {
				hitResult = true;
				thrownWeapons.remove(i);
				i--;
			} // end if
		} // end for loop
		return hitResult;
	} // end of checkForHits
} // end of class Enemy

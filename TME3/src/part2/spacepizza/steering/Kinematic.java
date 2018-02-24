/*
 * Kinematic.java
 * 
 * This class is used to hold the positional/rotational values for an object.  This includes the current velocity and
 * rotation of the object.  These values can be updated using a steering behaviour and time differential.  It serves an
 * important role in tracking the game entities.  It allows game entities to be drawn in the correct positions and to behave
 * in a defined manner according to the steering behaviours used to update the variables.  All code has been adapted from 
 * the textbook for this course.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package part2.spacepizza.steering;

import javax.vecmath.Vector2d;

public class Kinematic {
	
	public Vector2d position;
	public double orientation;
	public Vector2d velocity;
	public double rotation;
	public Vector2d target;
	
	// default constructor
	public Kinematic() {
		position = new Vector2d();
		orientation = 0.0;
		velocity = new Vector2d();
		rotation = 0.0;
		target = new Vector2d();
	} // end of default constructor
	
	// kinematic matching constructor
	public Kinematic(Kinematic k) {
		position = new Vector2d(k.position);
		orientation = k.orientation;
		velocity = new Vector2d(k.velocity);
		rotation = k.rotation;
		target = new Vector2d(k.target);
	} // end of kinematic matching constructor
	
	// updates the stored values based on the steering output and time, limited by the maximum speed
	public void update(SteeringOutput steering, double maxSpeed, double time) {
		position.scaleAdd(time, velocity, position);
		orientation += rotation * time;
		
		velocity.scaleAdd(time, steering.linear, velocity);
		rotation += steering.angular * time;
		
		// limit to max speed
		if (velocity.length() > maxSpeed) {
			velocity.normalize();
			velocity.scale(maxSpeed);
		} // end if
	} // end of update
	
	// returns a new orientation based on the current velocity
	public static double getNewOrientation(double current, Vector2d velocity) {
		if (velocity.length() > 0) {
			return Math.atan2(velocity.getX(), velocity.getY());
		} else {
			return current;
		}
	} // end of getNewOrientation
	
	// checks if the stored position is within range of the stored target position
	public Boolean inTargetRange() {
		if (Math.abs(position.x - target.x) < 5) {
			position.x = target.x;
		}
		
		if (Math.abs(position.y - target.y) < 5) {
			position.y = target.y;
		}
		
		if (Math.abs(position.x - target.x) < 10 &&
			Math.abs(position.y - target.y) < 10) {
			
			return true;
		} else {
			return false;
		} // end else
	} // end of inTargetRange
} // end of class Kinematic
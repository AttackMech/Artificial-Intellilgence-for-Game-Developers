/*
 * Separation.java
 * 
 * This is the 'separation' steering behaviour.  It calculates the linear acceleration needed to move equally away from
 * all targets.  Adapted from the class textbook for this course.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza.steering;

import javax.vecmath.Vector2d;

import spacepizza.Sprite;

public class Separation extends Behaviour {
	double threshold, decayCoeff, maxAccl;
	
	public Separation(double threshold, double decayCoeff, double maxAccl) {
		object = new Kinematic();
		this.threshold = threshold;
		this.decayCoeff = decayCoeff;
		this.maxAccl = maxAccl;
	} // end of constructor
	
	// returns the steering output for this behaviour using the array of targets and the midpoint of the flock
	public SteeringOutput getSteering(Sprite[] targets, int self, Vector2d mid) {
		SteeringOutput steer = new SteeringOutput();
		Vector2d direction = new Vector2d();
		double distance = 0.0;
		double strength = 0.0;
		
		setObject(targets[self].getKinematic());
		
		for (int i = 0; i < targets.length; i++) {
			// ignore self
			if (i == self) {
				continue;
			}
			direction.sub(targets[i].getKinematic().position, targets[self].getKinematic().position);
			distance = direction.length();
			
			if (distance < threshold) {
				strength = Math.min(decayCoeff / (distance * distance), maxAccl);
				direction.normalize();
				direction.scale(strength);
				steer.linear.add(direction);
			}
		} // end for loop
		
		direction.sub(mid, targets[self].getKinematic().position);
		distance = direction.length();
		
		if (distance < threshold) {
			strength = Math.min(decayCoeff / (distance * distance), maxAccl);
			direction.normalize();
			direction.scale(strength);
			steer.linear.add(direction);
		}
		
		return steer;
	} // end of getSteering
} // end of class Separation

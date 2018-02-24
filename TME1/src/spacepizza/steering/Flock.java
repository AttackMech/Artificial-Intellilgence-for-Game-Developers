/*
 * Flock.java
 * 
 * This is the 'flocking' behaviour.  It is actually a blend of the separation, seek, and velocity matching behaviours.
 * It blends each of these based on a weight value to achieve the final steering output and adjusts each member of the
 * flock to achieve the overall behaviour.  All code has been adapted from the source code supplied with the textbook for
 * this course.
 * 
 * NOTE - There are issues with the output produced by this class, please see the description document.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza.steering;

import javax.vecmath.Vector2d;

import spacepizza.Sprite;
import spacepizza.Toppings;

public class Flock {
	public static final double SEEK_WEIGHT = 0.06;
	public static final double SEP_WEIGHT = 1.0;
	public static final double VMATCH_WEIGHT = 0.002;
	
	private Seek seek;
	private Separation separation;
	private VelocityMatch velMatch;
	
	public Flock() {
		seek = new Seek(new Kinematic(), Toppings.MAX_ACCL);
		separation = new Separation(25.0, 25.0, Toppings.MAX_ACCL);
		velMatch = new VelocityMatch(new Kinematic(), Toppings.MAX_ACCL);
	} // end of constructor
	
	// calculates and returns the center point of all passed entities
	public Vector2d getFlockCentre(Sprite[] flock) {
		Vector2d centre = new Vector2d();

		for (Sprite sprite : flock) {
			centre.add(sprite.getKinematic().position);
		}
		centre.scale((double)(1.0 / flock.length));

		return centre;
	} // end of getFlockCentre
	
	// calculates and returns the average velocity of all passed entities
	public Vector2d getFlockAvgVelocity(Sprite[] flock) {
		Vector2d avgVelocity = new Vector2d();
		
		for (Sprite sprite : flock) {
			avgVelocity.add(sprite.getKinematic().velocity);
		}
		avgVelocity.scale((double)(1.0 / flock.length));
		
		return avgVelocity;
	} // end of getFlockAvgVelocity
	
	// calculates the cohesive steering by adjusting members towards the center of the flock
	public SteeringOutput getCohesionSteering(SteeringOutput so, Sprite[] flock, int member, Vector2d target) {
		SteeringOutput steer;
//		Vector2d massCentre = getFlockCentre(flock);
		seek.setObject(flock[member].getKinematic());
//		seek.target = massCentre;
		seek.target = target;

		steer = seek.getSteering();
		//steer.linear.add(flee.getSteering().linear);
		steer.linear.scale(SEEK_WEIGHT);
		so.linear.add(steer.linear);

		return so;
	} // end of getCohesionSteering
	
	// calculates the separation steering away from each flock memeber
	public SteeringOutput getSeparationSteering(SteeringOutput so, Sprite[] flock, int member, Vector2d target) {
		SteeringOutput steer;

		steer = separation.getSteering(flock, member, getFlockCentre(flock));
		steer.linear.scale(SEP_WEIGHT);
		so.linear.add(steer.linear);

		return so;
	} // end of getSeparationSteering
	
	// calculates the acceleration required to match the current velocity
	public SteeringOutput getVelMatchSteering(SteeringOutput so, Sprite[]flock, int member, Vector2d targetVelocity) {
		SteeringOutput steer;
		Vector2d avgVelocity = getFlockAvgVelocity(flock);
		
		velMatch.maxAccl = Toppings.MAX_ACCL;
		velMatch.setObject(flock[member].getKinematic());
		velMatch.targetVelocity = targetVelocity;
		
		steer = velMatch.getSteering();
		steer.linear.sub(avgVelocity, flock[member].getKinematic().velocity);// = avgVelocity - flock[member].kin.velocity;
		
		if (steer.linear.lengthSquared() > Toppings.MAX_ACCL * Toppings.MAX_ACCL) {
			steer.linear.normalize();
			steer.linear.scale(Toppings.MAX_ACCL);
		}
		
		steer.linear.scale(VMATCH_WEIGHT);
		so.linear.add(steer.linear);
		
		return so;
	} // end of getVelMatchSteering
	
	// adjusts each member of the flock using separation/cohesion/and vleocity mathching behaviours and blends them into one final value
	public void adjustFlock(Sprite[] flock, Kinematic leader, long time) {
		SteeringOutput steer;
	
		// check each flock member
		for (int j = 0; j < flock.length; j++) {
			steer = new SteeringOutput();
			steer = getSeparationSteering(steer, flock, j, leader.position);
			steer = getCohesionSteering(steer, flock, j, leader.position);
			steer = getVelMatchSteering(steer, flock, j, leader.velocity);
	
			// adjust to max value if over
			if (steer.linear.length() > Toppings.MAX_ACCL) {
				steer.linear.normalize();
				steer.linear.scale(Toppings.MAX_ACCL);
			}

			// update kinematic
			flock[j].getKinematic().update(steer, Toppings.MAX_SPD, time);
		} // end for loop
	} // end of adjustFlock
} // end of class Flock

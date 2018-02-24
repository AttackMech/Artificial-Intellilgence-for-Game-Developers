/*
 * Flee.java
 * 
 * This is the 'flee' steering behaviour.  It calculates the linear acceleration required to move away from a certain
 * point at maximum speed.  All code is adapted from the textbook for this course
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza.steering;

public class Flee extends Seek {

	public Flee(Kinematic object, double maxAccl) {
		super(object, maxAccl);
	} // end of constructor
	
	// returns the steering output for this behaviour	
	@Override
	public SteeringOutput getSteering() {
		SteeringOutput fleeSteer = new SteeringOutput();
		fleeSteer.linear.sub(object.position, target);
		fleeSteer.linear.normalize();
		fleeSteer.linear.scale(maxAccl);
		fleeSteer.angular = 0;
		return fleeSteer;
	} // end of getSteering
} // end of class Flee

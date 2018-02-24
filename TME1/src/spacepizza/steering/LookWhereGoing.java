/*
 * LookWhereGoing.java
 * 
 * This is the 'look where you're going' steering behaviour.  It subclasses the Align class and calculates the angular
 * acceleration required to match the current velocity.  All code is adapted from the textbook for this course
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza.steering;

public class LookWhereGoing extends Align {
	
	public LookWhereGoing(double maxAngleAccl, double maxRotation, double targetRadius, double slowRadius, double timeToTarget) {
		super(maxAngleAccl, maxRotation, targetRadius, slowRadius, timeToTarget);
	} // end of constructor
	
	// returns the steering output for this behaviour	
	@Override
	public SteeringOutput getSteering() {
		if (object.velocity.length() == 0) {
			return new SteeringOutput();
		}
		
		targetOrient = Math.atan2(-object.velocity.y, -object.velocity.x);
		return super.getSteering();
	} // end of getSteering	
} // end of class LookWhereGoing

/*
 * Behaviour.java
 * 
 * This is the base behaviour class used for all steering behaviours.  All code is adapted from the textbook for this course.
 * It holds the kinematic value used to calculate accelerational values and implements the GetSteer interface to produce a
 * SteeringOutput.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza.steering;

public class Behaviour implements GetSteer {
	protected Kinematic object;
	
	public Behaviour() {}
	
	// interface
	public SteeringOutput getSteering() {
		return new SteeringOutput();
	} // end of getSteering
	
	// set object to passed kinematic value
	public void setObject(Kinematic k) {
		object = k;
	} // end of setObject
} // end of class Behaviour

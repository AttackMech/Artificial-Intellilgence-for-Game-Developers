/*
 * SteeringOutput.java
 * 
 * This class is used to hold the linear and angular acceleration values used to steer and adjust a kinematic.  All code is
 * adapted from the textbook for this course.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza.steering;

import javax.vecmath.Vector2d;

public class SteeringOutput {

	public Vector2d linear;
	public double angular;
	
	public SteeringOutput() {
		linear = new Vector2d();
		angular = 0.0;
	} // end of constructor
} // end of class SteeringOutput

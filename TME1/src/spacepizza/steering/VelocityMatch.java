/*
 * VelocityMatch.java
 * 
 * This is the 'velocity match' steering behaviour.  It calculates the linear acceleration required to match a target
 * velocity.  All code is adapted from the class textbook for this course.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza.steering;

import javax.vecmath.Vector2d;

public class VelocityMatch extends Behaviour {
	Vector2d targetVelocity;
	double maxAccl;
	double timeToTarget = 0.1;
	
	public VelocityMatch(Kinematic object, double maxAccl) {
		super();
		this.object = object;
		this.maxAccl = maxAccl;
		targetVelocity = new Vector2d();
	} // end of constructor
	
	// returns the steering output for this behaviour
	@Override
	public SteeringOutput getSteering() {
		SteeringOutput steering = new SteeringOutput();
		
		steering.linear.sub(targetVelocity, object.velocity);
		steering.linear.scale(1 / timeToTarget);
		
		if (steering.linear.length() > maxAccl) {
			steering.linear.normalize();
			steering.linear.scale(maxAccl);
		}
		
		steering.angular = 0;
		
		return steering;
	} // end of getSteering
} // end of class VelocityMatch


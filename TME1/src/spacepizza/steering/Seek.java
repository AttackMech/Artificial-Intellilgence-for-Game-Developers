/*
 * Seek.java
 * 
 * This is the 'seek' steering behaviour.  It calculates the linear acceleration required to move to a certain point at
 * maximum speed.  All code is adapted from the textbook for this course
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza.steering;

import javax.vecmath.Vector2d;

public class Seek extends Behaviour {
	
	public Vector2d target;
	public double maxAccl;
	
	public Seek(Kinematic object, double maxAccl) {
		this.object = object;
		this.target = object.target;
		this.maxAccl = maxAccl;
	} // end of constructor
	
	// returns the steering output for this behaviour
	@Override
	public SteeringOutput getSteering() {
		SteeringOutput so = new SteeringOutput();
		
		so.linear.sub(target, object.position);
		
		if (so.linear.lengthSquared() > 0) {
			so.linear.normalize();
			so.linear.scale(maxAccl);
		}
		
		so.angular = 0;
		return so;
	} // end of getSteering()
	
	// returns the steering output based on a new acceleration value
	public SteeringOutput getSteering(double newAccl) {
		maxAccl = newAccl;
		return getSteering();
	} // end of getSteering(double)
	
	// sets the object and target to use in calcuating steering based on the passed kinematic
	@Override
	public void setObject(Kinematic k) {
		object = k;
		target = k.target;
	} // end of setObject
} // end of class Seek

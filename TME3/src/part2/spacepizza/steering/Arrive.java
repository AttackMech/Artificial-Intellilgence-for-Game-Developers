/*
 * Arrive.java
 * 
 * This is the 'arrive' steering behaviour.  It calculates the linear acceleration required to move to a certain point at
 * maximum speed, then slow down to a stop at the exact point.  All code is adapted from the textbook for this course.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package part2.spacepizza.steering;

import javax.vecmath.Vector2d;

public class Arrive extends Behaviour{
	
	public Vector2d target;
	public double maxAccl;
	public double maxSpeed;
	public double timeToTarget;
	public double targetRadius, slowRadius;
	
	public Arrive(Kinematic object, double maxAccl, double maxSpeed, double targetRadius, double slowRadius, double timeToTarget) {
		this.object = object;
		target = object.target;
		this.maxAccl = maxAccl;
		this.maxSpeed = maxSpeed;
		this.targetRadius = targetRadius;
		this.slowRadius = slowRadius;
		this.timeToTarget = timeToTarget;
	} // end of constructor
	
	// returns the steering output for this behaviour	
	public SteeringOutput getSteering() {
		SteeringOutput so = new SteeringOutput();
		
		Vector2d direction = new Vector2d();
		direction.sub(target, object.position);
		
		double distance = direction.length();
		
		Vector2d targetVelocity;
		double targetSpeed;
		
		if (distance < targetRadius) {
			return so;
		} else if (distance > slowRadius) {
			targetSpeed = maxSpeed;
		} else {
			targetSpeed = maxSpeed * distance / slowRadius;
		}
		
		targetVelocity = direction;
		targetVelocity.normalize();
		targetVelocity.scale(targetSpeed);
		
		so.linear.sub(targetVelocity, object.velocity);
		so.linear.scale(1 / timeToTarget);
		
		if (so.linear.length() > maxAccl) {
			so.linear.normalize();
			so.linear.scale(maxAccl);
		}
		
		so.angular = 0;
		return so;
	}
	
	// sets the object and target for this behaviour
	@Override
	public void setObject(Kinematic kin) {
		object = kin;
		target = kin.target;
	} // end of setObject
} // end of class Arrive

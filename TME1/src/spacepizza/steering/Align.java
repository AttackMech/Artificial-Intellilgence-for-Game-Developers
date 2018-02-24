/*
 * Align.java
 * 
 * This is the 'align' steering behaviour.  It calculates the angular acceleration required to rotate an object toward the
 * specified target.  All code is adapted from the textbook for this course.
 * 
 *  Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 *  Date: June 5, 2016
 * 
 */

package spacepizza.steering;

import javax.vecmath.Vector2d;

public class Align extends Behaviour {
	
	public Vector2d target;
	public double targetOrient;
	public double maxAngleAccl;
	public double maxRotation;
	public double timeToTarget;
	public double targetRadius, slowRadius;
	
	public Align(double maxAngleAccl, double maxRotation, double targetRadius, double slowRadius, double timeToTarget) {
		object = new Kinematic();
		target = object.target;
		targetOrient = 0;
		this.maxAngleAccl = maxAngleAccl;
		this.maxRotation = maxRotation;
		this.targetRadius = targetRadius;
		this.slowRadius = slowRadius;
		this.timeToTarget = timeToTarget;
	} // end of constructor
	
	// returns the steering output for this behaviour	
	@Override
	public SteeringOutput getSteering() {
		SteeringOutput so = new SteeringOutput();
		double rotation = targetOrient - object.orientation;
		double rotationDirection = mapToRange(rotation);  //  MAP TO RANGE -pi/pi
		double rotationSize = Math.abs(rotationDirection);
		double targetRotation;
		
		if (rotationSize < targetRadius) {
			return so;
		} 
		
		if (rotationSize > slowRadius) {
			targetRotation = maxRotation;
		} else {
			targetRotation = maxRotation * rotationSize / slowRadius;
		}
		targetRotation *= rotation / rotationSize;
			
		so.angular = targetRotation - object.rotation;
		so.angular /= timeToTarget;
			
		double angularAccl = Math.abs(so.angular);	
		if (angularAccl > maxAngleAccl) {
			so.angular /= angularAccl;
			so.angular *= maxAngleAccl;
		}
		so.linear = new Vector2d();
		
		return so;
	} // end of getSteering
	
	// returns a value that maps the supplied number to the range of -pi/pi
	private double mapToRange(double num) {
		double mapValue;
		
		if (num >= -Math.PI && num <= Math.PI) {
			return num;
		}
		
		if (num > Math.PI) {
			mapValue = num % (2 * Math.PI);
		} else {
			mapValue = num % (2 * Math.PI);
		}
		
		if (num >= -Math.PI && num <= Math.PI) {
			return mapValue;
		}
		
		if (mapValue > Math.PI) {
			return mapValue - (2 * Math.PI);
		} else {
			return mapValue + (2 * Math.PI);
		}
	} // end of mapToRange
	
	// sets the kinematic target and object along with the target orientation
	public void setObject(Kinematic kin, double targetOrient) {
		object = kin;
		target = kin.target;
		this.targetOrient = targetOrient;
	} // end of setObject
	
	// sets the target orientation to the supplied value
	public void setTargetOrient(double newOrient) {
		targetOrient = newOrient;
	} // end of setTargetOrieint
} // end of class Aligh

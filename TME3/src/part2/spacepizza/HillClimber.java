/*
 * HillClimber.java
 * 
 * This class features the main learning algorithm used for the game.  It is used exclusively to update the pizza cutter
 * position as it tracks the player position.  The algorithm is the same as described in the textbook for this course, and
 * the algorithm is described in detail in the accompanying description document for this assignment.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 23, 2016
 * 
 */

package part2.spacepizza;

import javax.vecmath.Vector2d;
import part2.spacepizza.steering.Kinematic;

public class HillClimber {
	private static final int STEPS = 3;
	private static final int PREDICT_TIME = 1000;
	
	int bestParameterIndex;
	int bestTweak;
	int[] steps;
	Kinematic kin;
	
	public HillClimber() {
		bestParameterIndex = -1;
		bestTweak = 0;
		steps = new int[STEPS * 2 + 1];
		for (int i = -STEPS; i <= STEPS; i++) {
			steps[i + STEPS] = i;
		}
		kin = new Kinematic();
	} // end of constructor
	
	// optimizeParameters function of the hill climbing algorithm outlined in the textbook
	public Vector2d optimizeParameters(Vector2d parameters) {
		int bestValue, value; 
		double currentParameter;
		// hold best parameter change so far
		bestParameterIndex = -1;
		bestTweak = 0;
		
		// initial best value is the value of he current parameters
		// no point changing to worse set
		bestValue = analyzeParameters(parameters);
		
		// Loop through each parameter
		for (int i = 0; i < 2; i++) {
			
			// store current parameter value
			if (i == 0) {
				currentParameter = parameters.getX();
			} else {
				currentParameter = parameters.getY();
			}
			
			// tweak both up and down
			for (int tweak : steps) {

				// apply the tweak
				if (i == 0) {
					parameters.x += tweak;
				} else {
					parameters.y += tweak;
				}
				// get the value of the function
				value = analyzeParameters(parameters);
				
				// is it best so far?
				if (value > bestValue) {
					
					// store it
					bestValue = value;
					bestParameterIndex = i;
					bestTweak = tweak;
				}
				
				// reset parameter to old value
				parameters.x = currentParameter;
			}
		}
		
		// gone through each parameter, check if good set
		if (bestParameterIndex >= 0) {
			
			// make parameter change permanent
			parameters.x += bestTweak;
		}
		
		// return modified parameters if found better set, or return original parameters
		return parameters;
	} // end of optimizeParameters(Vector2d)
	
	// hill climbing algorithm as seen in the textbook
	public Vector2d hClimb(Vector2d initialParameters) {
		// set the initial parameter settings
		Vector2d parameters = new Vector2d(initialParameters);
		
		// find the initial value for the initial parameters
		int value = analyzeParameters(parameters);
		
		// go through a number of steps
		for (int i = 0; i < STEPS; i++) {
			
			// get the new parameter settings
			parameters = optimizeParameters(parameters);
			
			// get the new value
			int newValue = analyzeParameters(parameters);
			
			// if can't improve, then end
			if (newValue <= value) {
				break;
			}
			
			// store the new value for the next iteration
			value = newValue;
		}
		
		// run out of steps or can't improve
		return parameters;
	} // end of hClimb(Vector2d)
	
	// analyzes the parameters passed for closeness to predicted position
	public int analyzeParameters(Vector2d parameters) {
		Vector2d projectPosition = new Vector2d();
		projectPosition.scaleAdd(PREDICT_TIME, kin.velocity, kin.position);
		projectPosition.sub(kin.position);
		return (int) Math.abs(projectPosition.length());
	} // end of analyzeParameters(Vector2d)
	
	// sets the kinematic used to represent the player
	public void setKinematic(Kinematic kin) { this.kin = new Kinematic(kin); }
} // end of class HillClimber

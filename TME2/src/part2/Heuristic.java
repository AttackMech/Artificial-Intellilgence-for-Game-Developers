/*
 * Heuristic.java
 * 
 * This class is the exact same as that used in part 1.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

import java.awt.Point;

public class Heuristic {
	private int gridSize;
	private Point goalPos;
	
	public Heuristic(int goal, int size) {
		gridSize = size;
		goalPos = new Point(goal % gridSize, goal - (goal % gridSize));
	} // end of constructor
	
	// returns the number of tiles between the passed tile and the goal tile
	public int estimate(int node) {
		int rows, cols;
		
		rows = Math.abs(goalPos.x - node % gridSize);
		cols = Math.abs(goalPos.y - (node - node % gridSize));
		
		return rows + cols;
	} // end of estimate(int)
} // end of class Heuristic

/*
 * MainPanel.java
 * 
 * This class is used as a heuristic for the pathfinding process.  It estimates the next tile to get closer to the goal
 * tile by determining the number of tiles to the goal tile from the current tile.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 8, 2016
 * 
 */

package part1;

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

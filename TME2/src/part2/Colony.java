/*
 * Colony.java
 * 
 * This class is used to control the entire colony of ants.  It holds all the ants in a Vector and determines movement
 * and behaviour, including pathfinding for each ant.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;

public class Colony {
	private int numAnts, boardSize, scaleFactor, home;
	private int antID, totalDead, totalAlive;
	private Vector<Ant> ants;
	private int[] food, water, poison;
	private Graph graph;
	private Heuristic heuristic;
	private Boolean removeDead;
	
	public Colony(int numAnts, int boardSize, int scaleFactor, int home) {
		this.numAnts = numAnts;
		this.boardSize = boardSize;
		this.scaleFactor = scaleFactor;
		this.home = home;
		
		antID = 0;
		removeDead = false;
		heuristic = new Heuristic(home, boardSize);
		
		setUpAnts();
	} // end of constructor
	
	// sets up the vector to hold the number of ants selected by the user
	private void setUpAnts() {
		ants = new Vector<Ant>();
		for (int i = 0; i < numAnts; i++) {
			ants.add(new Ant(++antID, boardSize, scaleFactor, home));
		}
	} // end of setUpAnts()
	
	// stores the array values passed to represent the special tiles on the game board
	public void addBoardElements(int[] food, int[] water, int[] poison, Graph graph) {
		this.food = food;
		this.water = water;
		this.poison = poison;
		this.graph = graph;
	} // end of addBoardElements(int[], int[], int[], Graph)
	
	// draws all ants and info to the screen
	public void draw(Graphics2D g2d) {
		// draw ants
		for (Ant ant : ants) {
			ant.draw(g2d);
		}
		// draw info
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, boardSize * scaleFactor, 10);
		g2d.setColor(Color.BLACK);
		String info = "COLONY INFO:  Ants born = " + antID + "  Alive = " + totalAlive + "  Dead = " + totalDead;
		g2d.drawString(info, 0, 10);
	} // end of draw(Graphics2D)
	
	// updates the ants behaviours and positions based on the current state
	public void updateAnts() {
		totalAlive = 0;
		
		// move living ants
		for (int i = 0; i < ants.size(); i++) {
			// check if alive
			if (!ants.get(i).checkAlive()) {
				totalDead++;
				continue;
			}
			
			totalAlive++;
			// move to new positions
			if (ants.get(i).isGoHome()) {
				ants.get(i).nextStepHome();
			} else {
				ants.get(i).nextRandomMove();
			}
			// check new positions
			if (checkPoison(ants.get(i).getElementPosition())) {
				ants.get(i).onPosion();
			} else if (checkFood(ants.get(i).getElementPosition())) {
				if (ants.get(i).onFood()) {
					ants.get(i).pathfindAStar(graph, heuristic);
				}
			} else if (checkWater(ants.get(i).getElementPosition())) {
				ants.get(i).onWater();
			} else if (ants.get(i).isGoHome() && ants.get(i).getElementPosition() == home) {
				ants.add(new Ant(++antID, boardSize, scaleFactor, home));
			}
		}
		totalDead = antID - totalAlive;
		
		// remove dead ants
		if (removeDead) {
			for (int i = 0; i < ants.size(); i++) {
				if (!ants.get(i).checkAlive()) {
					ants.remove(i--);
				}
			}
		}
		removeDead = !removeDead;
	} // end of update()
	
	// checks if the passed position is a food tile
	public boolean checkFood(int antPosition) {
		for (int i = 0; i < food.length; i++) {
			if (food[i] == antPosition) {
				return true;
			}
		}
		
		return false;
	} // end of checkFood
	
	// checks if the passed position is a water tile
	public boolean checkWater(int antPosition) {
		for (int i = 0; i < water.length; i++) {
			if (water[i] == antPosition) {
				return true;
			}
		}
		
		return false;
	} // end of checkWater
	
	// checks if the passed position is a poison tile
	public boolean checkPoison(int antPosition) {
		for (int i = 0; i < poison.length; i++) {
			if (poison[i] == antPosition) {
				return true;
			}
		}
		
		return false;
	} // end of checkPoison
	
	// returns the number of ants at a specified board position
	public int getAntsAtPosition(int position) {
		int returnVal = 0;
		for (Ant ant : ants) {
			if (ant.getElementPosition() == position) {
				returnVal++;
			}
		}
		return returnVal;
	} // end of getAntsAtPosition(int)
} // end of Colony

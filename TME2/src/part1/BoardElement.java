/*
 * BoardElement.java
 * 
 * This class is used to display the diffent tile types used in the game.  There are a total of 6 types, represented by
 * the static integer values below.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 8, 2016
 * 
 */

package part1;

import java.awt.Graphics2D;

public class BoardElement extends Sprite {
	public static final int START = 0;
	public static final int GOAL = 1;
	public static final int OPEN = 2;
	public static final int GRASS = 3;
	public static final int SWAMP = 4;
	public static final int OBSTACLE = 5;
	
	private int type, cost;
	private boolean start, goal;
	
	public BoardElement(int type) {
		super();
		if (validateTileType(type)) {
			this.type = type;
		} else {
			type = OPEN;
		}
		setTileImage();
		
		cost = getCostFromType(this.type);
		start = false;
		goal = false;
		
		cost = getCostFromType(type);
	} // end of constructor
	
	// set the image file to they associated type
	private void setTileImage() {
		switch (type) {
			case START:
				setImage("images/startTile.png");
				break;
			case GOAL:
				setImage("images/goalTile.png");
				break;
			case OPEN:
				setImage("images/openTile.png");
				break;
			case GRASS:
				setImage("images/grassTile.png");
				break;
			case SWAMP:
				setImage("images/swampTile.png");
				break;
			case OBSTACLE:
				setImage("images/obstacleTile.png");
				break;
		}
	} // end of setTitleImage()
	
	// sets the type and image to the specified parameter
	public void setTileImage(int type) {
		if (!validateTileType(type)) {
			return;
		}
		this.type = type;
		setTileImage();
		cost = getCostFromType(type);
	} // end of setTileImage(int)
	
	// returns the cost associated with a tile terrain type
	public int getCostFromType(int type) {
		switch (type) {
			case OPEN:
				return 0;
			case GRASS:
				return 1;
			case SWAMP:
				return 4;
			default:
				return -1;
		}
	} // end of getCOstFromType
	
	// check if the passed integer is a valid tile type
	public static boolean validateTileType(int type) {
		switch (type) {
			case START: case GOAL: case OPEN: case GRASS: case SWAMP: case OBSTACLE:
				return true;
			default:
				return false;
		}
	} // end of validateType(int)
	
	// draw the image to screen with start/goal tokens if necessary
	@Override
	public void drawImage(Graphics2D g2d) {
		super.drawImage(g2d);
		Sprite token;
		if (start) {
			token = new Sprite("images/???.png");
			token.drawImage(g2d);
		}
		if (goal) {
			token = new Sprite("images/???.png");
			token.drawImage(g2d);
		}
	} // end of drawImage(Graphics2D)
	
	// setters and getters
	public int getType() { return type; }
	
	public int getCost() { return cost; }
	
	public boolean isStart() { return start; }
	
	public boolean isGoal() { return goal; }
	
	public void removeAsStart() { start = false; }
	
	public void removeAsGoal() { goal = false; }
	
	// returns the string of the name associated with the tile type passed
	public static String getTileName(int type) {
		switch (type) {
			case START:
				return "START";
			case GOAL:
				return "GOAL";
			case OPEN:
				return "OPEN";
			case GRASS:
				return "GRASS";
			case OBSTACLE:
				return "OBSTACLE";
			default:
				return "NONE";
		}
	} // end of getTileName(int)
} // end of class BoardElement

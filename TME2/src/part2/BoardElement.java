/*
 * BoardElement.java
 * 
 * This class is the same as in part 1, but with different tile values as seen in the static ints listed below.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

public class BoardElement extends Sprite {
	public static final int HOME = 0;
	public static final int EMPTY = 1;
	public static final int WATER = 2;
	public static final int FOOD = 3;
	public static final int POISON = 4;
	public static final int NUM_TYPES = 5;
	
	private int type;
	
	public BoardElement(int type) {
		super();
		if (validateTileType(type)) {
			this.type = type;
		} else {
			type = EMPTY;
		}
		setTileImage();
	} // end of constructor
	
	// set the image file to they associated type
	private void setTileImage() {
		switch (type) {
			case HOME:
				setImage("images/homeTile.png");
				break;
			case EMPTY:
				setImage("images/openTile.png");
				break;
			case WATER:
				setImage("images/waterTile.png");
				break;
			case FOOD:
				setImage("images/foodTile.png");
				break;
			case POISON:
				setImage("images/poisonTile.png");
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
	} // end of setTileImage(int)
	
	// check if the passed integer is a valid tile type
	public static boolean validateTileType(int type) {
		if (type < HOME || type > POISON) {
			return false;
		} else {
			return true;
		}
	} // end of validateType(int)
	
	// returns the type of tile
	public int getType() { return type; }
	
} // end of class BoardElement

/*
 * BoardController.java
 * 
 * Similar to part 1, this class controls the game elements.  It handles the drawing and updating of the colony and game
 * board, as well as the Selector class used to select the starting number of ants.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 11, 2016
 * 
 */

package part2;

import java.awt.Graphics2D;

public class BoardController {
	public static final int GRID_SIZE = 20;
	public static final int ANT_MAX = 20;
	public static final int SELECT_NUM = 0;
	public static final int RUN = 1;
	
	private Board board;
	private Selector selector;
	private Colony colony;
	private int mode, antNum;
	
	public BoardController(int screenX, int screenY) {
		board = new Board(screenY, GRID_SIZE);
		selector = new Selector(screenX, screenY);
		mode = SELECT_NUM;
		antNum = 1;
	} // end of constructor
	
	// draws the game elements on the screen
	public void draw(Graphics2D g2d) {
		if (mode == SELECT_NUM) {
			selector.draw(g2d, antNum);
		} else {
			board.draw(g2d);
			drawAnts(g2d);
		}
	} // end of draw(Graphics2D)
	
	// draws all ants on screen
	private void drawAnts(Graphics2D g2d) {
		if (colony != null) {
			colony.draw(g2d);
		}
	} // end of drawAnts(Graphics2D)
	
	// updates the game if running the ant simulation
	public void update() {
		if (mode == SELECT_NUM) {
			return;
		} 
		// update ant positions
		colony.updateAnts();		
	} // end of update()
	
	// changes the mode if necessary when the the enter key is pressed
	public void checkEnter() {
		// check conditions
		if (mode != SELECT_NUM) {
			return;
		} 
		// change mode
		mode = RUN;
		// create ants
		colony = new Colony(antNum, GRID_SIZE, board.getScale(true), board.getHome());
		colony.addBoardElements(board.getAllFood(), board.getAllWater(), board.getAllPoison(), board.getGridGraph());
	} // end of checkEnter()
	
	// takes the appropriate action for a click based on the game mode
	public void checkMouseClick(int mx, int my) {
		if (mode != SELECT_NUM) {
			return;
		}
		// add or subtract number of ants
		if (selector.getClickValue(mx, my) != 0) {
			antNum += selector.getClickValue(mx, my);
		}

		// check for min/max number of ants
		if (antNum > ANT_MAX) {
			antNum = ANT_MAX;
		} else if (antNum < 1) {
			antNum = 1;
		}
	} // end of checkMouseClick(int, int)
} // end of checkMouseClic(int, int)

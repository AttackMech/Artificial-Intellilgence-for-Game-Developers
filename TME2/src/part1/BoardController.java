/*
 * BoardController.java
 * 
 * This class handles all the game elements.  It creates the Board and Details classes to display the game to the user.
 * It updates and draws these classes when called by the MainPanel class.  The Ant class is created and displayed after
 * the user has configured the board to their specifications.  This is used to display the pathfinding process.
 * 
 * Created by: Jason Bishop (3042012) for COMP 452 at Athabasca University
 * Date: June 8, 2016
 * 
 */

package part1;

import java.awt.Graphics2D;
import java.awt.Point;

public class BoardController {
	public static final int GRID_SIZE = 16;
	public static final int SELECT_START = 0;
	public static final int SELECT_GOAL = 1;
	public static final int SELECT_TERRAIN = 2;
	public static final int RUN = 3;
	
	private Board board;
	private Details details;
	private Ant ant;
	private int mode;
	
	public BoardController(int screenX, int screenY) {
		board = new Board(screenY, GRID_SIZE);
		details = new Details(screenX / 3, screenY);
		mode = SELECT_START;
	} // end of constructor
	
	// draws the game elements on the screen
	public void draw(Graphics2D g2d) {
		// determine if cursor is drawn
		boolean drawCursor = false;
		if (mode != RUN) {
			drawCursor = true;
		}

		board.draw(g2d, drawCursor);
		details.draw(g2d, mode);

		// show ant and pathfinding
		if (mode == RUN && ant != null) {
			board.setLists(ant.getOpenList(), ant.getClosedList());
			if (!ant.isRunning()) {
				ant.draw(g2d);
				details.setPathString(ant.getPathString());
				details.draw(g2d, mode);
			} // end if
		} //end if
	} // end of draw(Graphics2D)
	
	// changes the mode if necessary when the the enter key is pressed
	public void checkEnter() {
		// check conditions
			System.out.println("checking ENTER press... mode = " + mode);
		if (mode != SELECT_START && mode != SELECT_GOAL && mode != SELECT_TERRAIN) {
			return;
		} else if (mode == SELECT_START && board.getStart() == -1) {
			return;
		} else if (mode == SELECT_GOAL && board.getGoal() == -1) {
			return;
		}
		// change mode
		mode++;
		if (mode == SELECT_TERRAIN) {
			board.fillGameGrid(2, true);
		} else if (mode == RUN) {
			board.setUpConnections();
			ant = new Ant(board.getScale(true), board.getStartPoint(true));
			ant.pathfindAStar(board.getGridGraph(), board.getStart(), board.getGoal(), new Heuristic(board.getGoal(), GRID_SIZE));
		}
			System.out.println("mode = " + mode);
	} // end of checkEnter()
	
	// track the position of the mouse for input during setup
	public void trackMouse(Point mouse) {
		if (mode == RUN) {
			return;
		}
		board.setCursor(mouse);
	} // end of trackMouse(Point)
	
	// takes the appropriate action for a click based on the game mode
	public void checkMouseClick(int mx, int my) {
			System.out.println("mouse click at " + mx + "," + my);
		if (board.pointToElement(new Point(mx, my)) == -1) {
			return;
		}
		switch (mode) {
			case SELECT_START:
				board.setStart(mx, my);
				break;
			case SELECT_GOAL:
				board.setGoal(mx, my);
				break;
			case SELECT_TERRAIN:
				board.setTerrain(mx, my);
				break;
			default:
				return;
		}
	} // end of checkMouseClick(int, int)
} // end of class BoardController
